(ns groom.queries
  (:require [clojure.pprint :refer [pprint]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [groom.specs.v1]))

(declare graphql-field)

(s/fdef graphql-query-params
  :args (s/cat :params (s/spec ::groom.specs.v1/params))
  :ret string?)

(defn graphql-query-params
  [params]
  (let [gql-params (map (fn [[k v]]
                          (str (str/replace (name k) "?" "$") ": " (name v)))
                        params)]
    (str "(" (str/join ", " gql-params) ")")))

(s/fdef graphql-directive
  :args (s/cat :directive ::groom.specs.v1/directive)
  :ret string?)

(defn graphql-directive
  [directive]
  (str/replace (name directive) "+" "@"))

(s/fdef graphql-bound-query-params
  :args (s/cat :params (s/spec ::groom.specs.v1/params))
  :ret string?)

(defn graphql-bound-query-params
  [params]
  (let [gql-params (map (fn [k]
                          (str (str/replace (name k) "?" "") ": "
                               (str/replace (name k) "?" "$")))
                        (keys params))]
    (str "(" (str/join "," gql-params) ")")))

(s/fdef graphql-simple-field
  :args (s/cat :field map?)
  :ret string?)

(defn graphql-simple-field
  [{:keys [name directives]}]
  (->> [(clojure.core/name name)
        (some->> directives (map graphql-directive) (str/join " "))]
       (remove nil?)
       (str/join " ")))

(s/fdef graphql-nested-field
  :args (s/cat :field map?)
  :ret string?)

(defn graphql-nested-field
  [field]
  "TODO: NESTED FIELD")

(s/fdef graphql-fragment-field
  :args (s/cat :field map?)
  :ret string?)

(defn graphql-fragment-field
  [{:keys [type fields]}]
  (->> ["..." "on" (name type) "{"
        (some->> fields (map graphql-field) (str/join " "))
        "}"]
       (remove nil?)
       (str/join " ")))

(s/fdef graphql-field
  :args (s/cat :field (s/or :simple (s/tuple #{:simple-field} any?)
                            :nested (s/tuple #{:nested-field} any?)))
  :ret string?)

(defn graphql-field
  [[type field]]
  (case type
    :simple-field (graphql-simple-field field)
    :nested-field (graphql-nested-field field)
    :fragment-field (graphql-fragment-field field)))

(s/fdef graphql-query
  :args (s/cat :query (s/spec ::groom.specs.v1/query))
  :ret string?)

(defn graphql-query
  [query]
  (let [query        (s/conform ::groom.specs.v1/query query)
        params       (some->> query :params graphql-query-params)
        bound-params (some-> query :params graphql-bound-query-params)
        directives   (some->> query :directives (map graphql-directive) (str/join " "))
        fields       (some->> query :fields (map graphql-field) (str/join " "))]
    (->> ["query" (:name query) params directives "{"
          (:name query) bound-params "{ " fields " }"
          "}"]
         (remove nil?)
         (str/join " "))))
