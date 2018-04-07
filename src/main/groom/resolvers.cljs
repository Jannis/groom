(ns groom.resolvers
  (:require [clojure.spec.alpha :as s]
            [goog.object :as gobj]
            [goog.string :as gstring]
            [goog.string.format]
            [graphql-tag :as gql]
            [groom.util.js :as js-util]))

(s/fdef update-field
  :args (s/cat :typename keyword?
               :field keyword?
               :f fn?)
  :ret fn?)

(defn update-field
  [typename field f]
  (let [str-typename (name typename)
        str-field    (name field)
        fragment     (gql (gstring/format "fragment %s on %s { %s }"
                                          str-field str-typename str-field))]
    (fn [_ params cache-info extra]
      (let [id            (gobj/get params "id")
            get-cache-key (gobj/get cache-info "getCacheKey")
            cache         (gobj/get cache-info "cache")]
        (let [fragment-id     (get-cache-key #js {:id id :__typename str-typename})
              updated-entity  (-> cache
                                  (.readFragment #js {:fragment fragment
                                                      :id fragment-id})
                                  (js->clj :keywordize-keys true)
                                  (update field f)
                                  (clj->js))]
          (.writeData cache #js {:id fragment-id :data updated-entity}))))))
