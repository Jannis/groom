(ns groom.bindings
  (:require [clojure.core.specs.alpha :as core-specs]
            [clojure.spec.alpha :as s]
            [groom.specs.v1]))

(s/fdef query-bindings
  :args (s/cat :queries vector?) ; FIXME Add spec for conformed queries
  :ret ::core-specs/map-bindings)

(defn query-bindings
  [queries]
  (reduce (fn [bindings query]
            (merge bindings {{(:name query) (keyword (name (:name query)))} :data}))
          '{{loading? :loading} :data} queries))

(s/fdef with-query-bindings*
  :args (s/cat :queries vector? ; FIXME Add spec for conformed queries
               :props (s/map-of keyword? any?)
               :body any?)
  :ret any?)

(defn with-query-bindings*
  [queries props body]
  (let [bindings (query-bindings queries)]
    `(let [~bindings ~props]
       ~@body)))

(defmacro with-query-bindings
  [queries props & body]
  (with-query-bindings* queries props body))

(s/fdef mutation-bindings
  :args (s/cat :mutations vector?) ; FIXME Add spec for conformed mutations
  :ret ::core-specs/map-bindings)

(defn mutation-bindings
  [mutations]
  (reduce (fn [bindings mutation]
            (merge bindings {(:name mutation) (keyword (name (:name mutation)))}))
          {} mutations))

(s/fdef with-mutation-bindings*
  :args (s/cat :mutations vector? ; FIXME Add spec for conformed mutations
               :props (s/map-of keyword? any?)
               :body any?)
  :ret any?)

(defn with-mutation-bindings*
  [mutations props body]
  (let [bindings (mutation-bindings mutations)]
    `(let [~bindings ~props]
       ~@body)))

(defmacro with-mutation-bindings
  [mutations props & body]
  (with-mutation-bindings* mutations props body))
