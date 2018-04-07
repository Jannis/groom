(ns groom.specs.graphql.apollo.link-http-common
  (:require [clojure.spec.alpha :as s]))

(s/def ::credentials string?)
(s/def ::fetch any?)
(s/def ::fetch-options (s/map-of keyword? any?))
(s/def ::headers (s/map-of keyword? any?))
(s/def ::include-extensions? boolean?)
(s/def ::uri (s/or :string string? :function fn?))

