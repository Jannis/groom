(ns groom.specs.graphql.apollo.cache-inmemory
  (:require [clojure.spec.alpha :as s]))

;;;; Options

(s/def ::data-id-from-object fn?)

(s/def ::fragment-matcher
  (s/keys :req-un [::fragment-matcher-match]))

(s/def ::add-typename? boolean?)

(s/def ::type-name keyword?)
(s/def ::field-name keyword?)
(s/def ::cache-resolver fn?)

(s/def ::cache-redirects
  (s/map-of ::type-name (s/map-of ::field-name ::cache-resolver)))

(s/def ::store-factory fn?)

(s/def ::opts
  (s/keys :opt-un [::data-id-from-object
                   ::fragment-matcher
                   ::add-typename?
                   ::cache-redirects
                   ::store-factory]))

;;;; In memory cache

(s/def ::in-memory-cache any?)

;;;; Normalized cache object

(s/def ::__typename string?)

(s/def ::store-object
  (s/and (s/map-of ::store-field-key ::store-value)
         (s/keys :opt-un [::__typename])))

(s/def ::normalized-cache-object (s/map-of ::data-id ::store-object))
