(ns groom.specs.v1
  (:require [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]
            [clojure.test.check.generators]
            [clojure.string :as str]))

;;;; Common

(s/def ::directive
  (s/with-gen
    (s/and symbol?
           #(str/starts-with? (name %) "+"))
    #(gen/fmap (fn [sym]
                 (symbol (str "+" (name sym))))
               (gen/symbol))))

(s/def ::param-name
  (s/with-gen
    (s/and symbol?
           #(str/starts-with? (name %) "?"))
    #(gen/fmap (fn [sym]
                 (symbol (str "?" (name sym))))
               (gen/symbol))))

(s/def ::param-type
  '#{ID! String!})

(s/def ::params
  (s/map-of ::param-name
            ::param-type
            :min-count 1
            :gen-max 5))

(s/def ::simple-field
  (s/cat :name keyword?
         :directives (s/* ::directive)))

(s/def ::nested-field-param-name
  symbol?)

(s/def ::nested-field-param-value
  any?)

(s/def ::nested-field-params
  (s/map-of ::nested-field-param-name
            ::nested-field-param-value
            :min-count 1
            :gen-max 5))

(s/def ::nested-field
  (s/cat :name ::simple-field
         :params (s/? ::nested-field-params)
         :directives (s/* ::directive)
         :fields ::fields))

(s/def ::fragment-field
  (s/cat :dots #{'...}
         :on #{'on}
         :type symbol?
         :fields ::fields))

(s/def ::fields
  (s/with-gen
    (s/spec (s/+ (s/alt :simple-field ::simple-field
                        :nested-field ::nested-field
                        :fragment-field ::fragment-field)))
    #(gen/fmap concat
               (gen/vector (gen/one-of
                            [(s/gen ::simple-field)
                             (s/gen ::nested-field)])))))

;;;; Queries

(s/def ::query-name
  symbol?)

(s/def ::query
  (s/cat :name ::query-name
         :params (s/? ::params)
         :directives (s/* ::directive)
         :fields ::fields))

(s/def ::queries
  (s/coll-of ::query
             :kind vector?
             :min-count 1
             :gen-max 5))

;;;; Mutations

(s/def ::mutation-name
  symbol?)

(s/def ::mutation-call-name
  symbol?)

(s/def ::mutation-call-param-name
  symbol?)

(s/def ::mutation-call-params
  (s/map-of ::mutation-call-param-name
            ::param-name
            :min-count 1
            :gen-max 5))

(s/def ::mutation-call
  (s/cat :name ::mutation-call-name
         :params (s/? ::mutation-call-params)
         :directives (s/* ::directive)
         :fields (s/? ::fields)))

(s/def ::mutation
  (s/cat :name ::mutation-name
         :params (s/? ::params)
         :call (s/spec ::mutation-call)))

(s/def ::mutations
  (s/coll-of ::mutation
             :kind vector?
             :min-count 1
             :gen-max 5))
