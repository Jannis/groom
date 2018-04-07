(ns groom.specs.graphql.apollo.link-state
  (:require [clojure.spec.alpha :as s]
            [groom.specs.graphql.apollo.cache :as cache-specs]
            [groom.specs.graphql.apollo.link :as common-specs]))

(s/def ::resolvers any?)
(s/def ::defaults any?)
(s/def ::type-defs (s/or :one string? :many (s/coll-of string?)))

(s/def ::opts
  (s/keys :req-un [::resolvers]
          :opt-un [::cache-specs/cache
                   ::defaults
                   ::type-defs]))
