(ns groom.specs.graphql.apollo.client
  (:require [clojure.spec.alpha :as s]))

(s/def ::document-node any?) ; TODO

(s/def ::context any?)

(s/def ::error-policy #{:none
                        :ignore
                        :all})

(s/def ::fetch-policy #{:cache-first
                        :cache-and-network
                        :network-only
                        :cache-only
                        :no-cache
                        :standby})

(s/def ::variables (s/map-of keyword? any?))

(s/def ::query ::document-node)

(s/def ::pure-query-options
  (s/keys :req-un [::query]
          :opt-un [::variables]))

(s/def ::fetch-query-description
  (s/coll-of (s/or :string string?
                   :options ::pure-query-options)
             :kind vector?))

(s/def ::refetch-queries
  (s/or :function fn?
        :description ::refetch-query-description))

(s/def ::update fn?)

(s/def ::mutate-options
  (s/keys :req-un [::document-node]
          :opt-un [::context
                   ::error-policy
                   ::fetch-policy
                   ::optimistic-response
                   ::refetch-queries
                   ::update
                   ::update-queries
                   ::variables]))

(s/def ::link any?)

(s/def ::cache any?)

(s/def ::ssr-mode boolean?)

(s/def ::ssr-force-fetch-delay pos?)

(s/def ::connect-to-dev-tools? boolean?)

(s/def ::query-deduplication? boolean?)

(s/def ::default-options
  (s/keys :opt-un [::mutate-options
                   ::query-options
                   ::watch-query-options]))

(s/def ::opts
  (s/keys :req-un [::link
                   ::cache]
          :opt-un [::ssr-mode?
                   ::ssr-force-fetch-delay
                   ::connect-to-dev-tools?
                   ::query-deduplication?
                   ::default-options]))
