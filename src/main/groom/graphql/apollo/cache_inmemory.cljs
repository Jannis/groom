(ns groom.graphql.apollo.cache-inmemory
  (:require [apollo-cache-inmemory :as aci]
            [clojure.spec.alpha :as s]
            [groom.util.js :as js-util]
            [groom.specs.graphql.apollo.cache-inmemory :as specs]))

(s/fdef in-memory-cache
  :args (s/cat :opts (s/? ::specs/opts))
  :ret ::specs/in-memory-cache)

(defn in-memory-cache
  ([]
   (in-memory-cache {}))
  ([opts]
   (new aci/InMemoryCache (js-util/->js opts))))

(s/fdef restore
  :args (s/cat :cache ::specs/in-memory-cache
               :data ::specs/normalized-cache-object)
  :ret ::specs/in-memory-cache)

(defn restore
  [cache data]
  (.restore cache (js-util/->js data)))

(s/fdef extract
  :args (s/cat :cache ::specs/in-memory-cache
               :optimistic? boolean?)
  :ret ::specs/normalized-cache-object)

(defn extract
  [cache optimistic?]
  (.extract cache optimistic?))

;; TODO:
;;
;; - read
;; - write
;; - diff
;; - watch
;; - evict
;; - reset
;; - remove-optimistic
;; - perform-transaction
;; - record-optimistic-transaction
;; - transform-document
;; - read-query
;; - read-fragment
;; - write-query
;; - write-fragment
;; - broadcast-watches
;;
;; Plus all public functions in the base ApolloCache class.
