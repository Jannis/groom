(ns groom.mutations
  (:require [clojure.pprint :refer [pprint]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [groom.queries :as q]
            [groom.specs.v1]))

(s/fdef graphql-bound-mutation-params
  :args (s/cat :params (s/spec ::groom.specs.v1/mutation-call-params))
  :ret string?)

(defn graphql-bound-mutation-params
  [params]
  (let [gql-params (map (fn [[k v]]
                          (str (name k) ": "
                               (str/replace (name v) "?" "$")))
                        params)]
    (str "(" (str/join "," gql-params) ")")))

(s/fdef graphql-mutation
  :args (s/cat :mutation (s/spec ::groom.specs.v1/mutation))
  :ret string?)

(defn graphql-mutation
  [mutation]
  (let [mutation     (s/conform ::groom.specs.v1/mutation mutation)
        params       (some-> mutation :params q/graphql-query-params)
        bound-params (some->> mutation :call :params graphql-bound-mutation-params)
        directives   (some->> mutation :call :directives
                              (map q/graphql-directive)
                              (str/join " "))
        fields       (some->> mutation :call :fields (map q/graphql-field))]
    (->> ["mutation" (:name mutation) params "{"
          (:name (:call mutation)) bound-params directives
          (when (seq fields)
            (str "{ " (str/join " " fields) " }"))
          "}"]
         (remove nil?)
         (str/join " "))))
