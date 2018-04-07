(ns groom.graphql.apollo.client
  (:require [apollo-client :as ac]
            [clojure.spec.alpha :as s]
            [groom.util.js :as js-util]
            [groom.specs.graphql.apollo.client :as specs]))

(s/fdef apollo-client
  :args (s/cat :opts ::specs/opts)
  :ret fn?)

(defn apollo-client [opts]
  (new ac/ApolloClient (js-util/->js opts)))
