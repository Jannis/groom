(ns groom.queries
  (:require [clojure.pprint :refer [pprint]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [groom.specs.v1 :as v1]))

(declare graphql-field)

;; TODO: How to spec/call this?
;; - Write a specs for the conformed ::v1/params? Not really, this gets tedious :/
;; - Call this function with unformed params? Also not really, I think we want to pass the conformed AST around

;; (s/fdef graphql-query-params
;;   :args (s/cat :params (s/spec ::v1/params))
;;   :ret string?)

(defn graphql-query-params
  [params]
  (let [gql-params (map (fn [[k v]]
                          (str (str/replace (name k) "?" "$") ": "
                               (s/unform ::v1/param-type v)))
                        params)]
    (str "(" (str/join ", " gql-params) ")")))

(s/fdef graphql-directive
  :args (s/cat :directive ::v1/directive)
  :ret string?)

(defn graphql-directive
  [directive]
  (str/replace (name directive) "+" "@"))

;; TODO: Same as the s/fdef for graphql-query-params above.

;; (s/fdef graphql-bound-query-params
;;   :args (s/cat :params (s/spec ::v1/params))
;;   :ret string?)

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
  :args (s/cat :query (s/spec ::v1/query))
  :ret string?)

(defn graphql-query
  [query]
  (let [query        (s/conform ::v1/query query)
        params       (some->> query :params graphql-query-params)
        bound-params (some-> query :params graphql-bound-query-params)
        directives   (some->> query :directives (map graphql-directive) (str/join " "))
        fields       (some->> query :fields (map graphql-field) (str/join " "))]
    (->> ["query" (:name query) params directives "{"
          (:name query) bound-params "{ " fields " }"
          "}"]
         (remove nil?)
         (str/join " "))))
