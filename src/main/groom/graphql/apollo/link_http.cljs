(ns groom.graphql.apollo.link-http
  (:require [apollo-link-http :as alh]
            [clojure.spec.alpha :as s]
            [groom.util.js :as js-util]
            [groom.specs.graphql.apollo.link :as link-specs]
            [groom.specs.graphql.apollo.link-http :as specs]))

(s/fdef http-link
  :args (s/cat :opts ::specs/opts)
  :ret ::link-specs/link)

(defn http-link [opts]
  (new alh/HttpLink (js-util/->js opts)))
