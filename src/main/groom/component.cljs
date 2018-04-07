(ns groom.component
  (:require [clojure.pprint :refer [pprint]]
            [clojure.spec.alpha :as s]
            [clojure.walk :refer [postwalk]]
            [goog.object :as gobj]
            [graphql-tag :as gql]
            [groom.queries :as q]
            [groom.mutations :as m]
            [groom.specs.v1]
            [groom.specs.sablono.v1]
            [react]
            [react-apollo :refer [compose graphql]]
            [sablono.core :refer [html]]))


;;;; Prepare for Sablono

(gobj/set js/window "React" react)


;;;; Component rendering

(defn- realize-component
  ([component]
   (realize-component component {}))
  ([component props]
   (react/createElement component (clj->js props))))

(s/fdef process-sablono
  :args (s/cat :x any?)
  :ret ::groom.specs.sablono.v1/sablono-tree)

(defn- process-sablono
  [x]
  (cond->> x
    (and (vector? x)
         (fn? (first x))) (apply realize-component)))

(s/fdef process-render-tree
  :args (s/cat :tree ::groom.specs.sablono.v1/groom-sablono-tree)
  :ret ::groom.specs.sablono.v1/sablono-tree)

(defn- process-render-tree
  [tree]
  (postwalk process-sablono tree))

(s/fdef render-wrapper
  :args (s/cat :opts ::component-opts)
  :ret fn?)

(defn- render-wrapper
  [{:keys [render]}]
  (fn [js-props]
    (let [props (js->clj js-props :keywordize-keys true)]
      (html (process-render-tree (render props))))))


;;;; Queries wrapper

(s/fdef compose-queries
  :args (s/cat :queries (s/coll-of string?))
  :ret fn?)

(defn- compose-queries
  [queries]
  (->> queries
       (map (fn [query]
              (graphql (gql query))))
       (into-array)
       (compose)))

(s/fdef queries-wrapper
  :args (s/cat :render fn?
               :opts ::component-opts)
  :ret fn?)

(defn- queries-wrapper
  [render {:keys [name queries]}]
  (cond->> render
    (seq queries) ((compose-queries (map q/graphql-query queries)))))


;;;; Mutations wrapper

(s/fdef compose-mutations
  :args (s/cat :mutations :groom.specs.v1/mutations)
  :ret fn?)

(defn- compose-mutations
  [mutations]
  (->> mutations
       (map (fn [mutation]
              (graphql (gql (m/graphql-mutation mutation))
                       (let [mutation' (s/conform ::groom.specs.v1/mutation mutation)]
                         #js {:name (:name mutation')}))))
       (into-array)
       (compose)))

(s/fdef mutations-wrapper
  :args (s/cat :render fn?
               :opts ::component-opts)
  :ret fn?)

(defn- mutations-wrapper
  [render {:keys [mutations]}]
  (cond->> render
    (seq mutations) ((compose-mutations mutations))))


;;;; Component definition

(s/def ::render fn?)
(s/def ::name symbol?)
(s/def ::component-opts
  (s/keys :req-un [::render]
          :opt-un [::name
                   ::groom.specs.v1/queries
                   ::groom.specs.v1/mutations]))

(s/fdef component
  :args (s/cat :opts ::component-opts)
  :ret  fn?)

(defn component
  [opts]
  (-> (render-wrapper opts)
      (queries-wrapper opts)
      (mutations-wrapper opts)))
