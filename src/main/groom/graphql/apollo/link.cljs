(ns groom.graphql.apollo.link
  (:require [apollo-link]
            [clojure.spec.alpha :as s]
            [groom.specs.graphql.apollo.link :as specs]))

(s/fdef from
  :args (s/coll-of ::specs/link)
  :ret ::specs/link)

(defn from [links]
  (apollo-link/from (into-array links)))
