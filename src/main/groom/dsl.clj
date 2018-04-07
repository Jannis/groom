(ns groom.dsl
  (:require [clojure.spec.alpha :as s]
            [groom.bindings]
            [groom.specs.v1]))

(s/def ::component-name symbol?)

(s/def ::defcomponent-queries
  (s/spec (s/cat :name #{'queries}
                 :body :groom.specs.v1/queries)))

(s/def ::defcomponent-mutations
  (s/spec (s/cat :name #{'mutations}
                 :body :groom.specs.v1/mutations)))

(s/def ::defcomponent-render
  (s/spec (s/cat :name #{'render}
                 :body any?)))

(s/def ::defcomponent-args
  (s/cat :name ::component-name
         :forms (s/spec (s/cat :queries (s/? ::defcomponent-queries)
                               :mutations (s/? ::defcomponent-mutations)
                               :render ::defcomponent-render))))

(defn make-component-definition
  [name args]
  (let [forms                 (:forms args)
        conforming-queries    (:body (:queries forms))
        conforming-mutations  (:body (:mutations forms))
        queries               (s/unform :groom.specs.v1/queries
                                        (:body (:queries forms)))
        mutations             (s/unform :groom.specs.v1/mutations
                                        (:body (:mutations forms)))
        render                (:body (:render forms))]
    `(def ~(symbol name)
       (groom.component/component
        ~(cond-> `{:name '~name
                   :render (fn [~'props]
                             ~(if (seq conforming-queries)
                                (if (seq conforming-mutations)
                                  `(groom.bindings/with-mutation-bindings
                                     ~conforming-mutations ~'props
                                     (groom.bindings/with-query-bindings
                                       ~conforming-queries ~'props
                                       ~render))
                                  `(groom.bindings/with-query-bindings
                                     ~conforming-queries ~'props
                                     ~render))
                                (if (seq conforming-mutations)
                                  `(groom.bindings/with-mutation-bindings
                                     ~conforming-mutations ~'props
                                     ~render)
                                  render)))}
           (seq queries)   (assoc :queries `'~queries)
           (seq mutations) (assoc :mutations `'~mutations))))))

(s/fdef defcomponent*
   :args ::defcomponent-args
   :ret any?)

(defn defcomponent*
  ([name forms]
   (defcomponent* name forms))
  ([name forms env]
   (let [args (if (s/valid? ::defcomponent-args [name forms])
                (s/conform ::defcomponent-args [name forms])
                (let [explanation (s/explain-str ::defcomponent-args [name forms])]
                  (throw (new Exception explanation))))]
     (make-component-definition name args))))

(defmacro defcomponent
  [name & forms]
  (defcomponent* name forms &env))
