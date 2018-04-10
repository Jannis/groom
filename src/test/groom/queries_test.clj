(ns groom.queries-test
  (:require [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]
            [groom.queries :as q]))

(stest/instrument)

(deftest test-param-type-not-null
  (is (= (q/graphql-query '(node {?id ID!} [:id :__typename]))
         "query node ($id: ID!) { node (id: $id) {  id __typename  } }")))

(deftest test-param-type-not-null-list
  (is (= (q/graphql-query '(nodes {?ids [ID!]} [:id :__typename]))
         "query nodes ($ids: [ID!]) { nodes (ids: $ids) {  id __typename  } }")))

(deftest test-param-type-not-null-list-not-null
  ;; TODO: How to represent not nullable lists?
  #_(is (= (q/graphql-query '(nodes {?ids [ID!!]} [:id :__typename]))
           "query nodes ($ids: [ID!]!) { nodes (ids: $ids) {  id __typename  } }"))
  #_(is (= (q/graphql-query '(nodes {?ids (ID!)} [:id :__typename]))
           "query nodes ($ids: [ID!]!) { nodes (ids: $ids) {  id __typename  } }")))
