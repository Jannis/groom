(ns groom.specs.sablono.v1
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::sablono-tag
  #{:h1 :h2 :h3 :h4 :h5 :h6
    :div :p :span
    :ol :ul :li
    :dl :dt :dd})

(s/def ::sablono-args
  (s/map-of keyword? any? :gen-max 5))

(s/def ::sablono-element
  (s/and vector?
         (s/cat :element ::sablono-tag
                :args (s/? ::sablono-args)
                :children (s/* ::sablono-tree))))

(s/def ::sablono-tree
  (s/or :element ::sablono-element
        :any any?))

(s/def ::groom-sablono-tag
  (s/or :tag ::sablono-tag
        :component fn?))

(s/def ::groom-sablono-element
  (s/and vector?
         (s/cat :tag ::groom-sablono-tag
                :args (s/? ::sablono-args)
                :children (s/* ::groom-sablono-tree))))

(s/def ::groom-sablono-tree
  (s/or :element ::groom-sablono-element
        :any any?))
