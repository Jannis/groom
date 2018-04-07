(ns groom.graphql.apollo.link-state
  (:require [apollo-link-state :as als]
            [clojure.spec.alpha :as s]
            [groom.util.js :as js-util]
            [groom.specs.graphql.apollo.link :as common-specs]
            [groom.specs.graphql.apollo.link-state :as specs]))

(s/fdef with-client-state
  :args (s/cat :opts ::specs/opts)
  :ret ::common-specs/link)

(defn with-client-state
  [opts]
  (als/withClientState (js-util/->js opts)))
