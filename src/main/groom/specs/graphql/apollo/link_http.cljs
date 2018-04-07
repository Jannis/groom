(ns groom.specs.graphql.apollo.link-http
  (:require [clojure.spec.alpha :as s]
            [groom.specs.graphql.apollo.link-http-common :as common-specs]))

(s/def ::use-GET-for-queries? boolean?)

(s/def ::opts
  (s/keys :req-un []
          :opt-un [::common-specs/credentials
                   ::common-specs/fetch
                   ::common-specs/fetch-options
                   ::common-specs/headers
                   ::common-specs/include-extensions?
                   ::common-specs/uri
                   ::use-GET-for-queries?]))
