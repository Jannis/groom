(ns groom.util.js
  (:require [camel-snake-kebab.core :refer [->camelCase ->kebab-case]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.walk :refer [walk]]))

(s/fdef capitalization-preserving-camel-case
  :args (s/cat :s string?)
  :ret string?)

(defn- capitalization-preserving-camel-case
  [s]
  (let [c (subs s 0 1)]
    (if (= c (string/upper-case c))
      (str c (->camelCase (subs s 1)))
      (->camelCase s))))

(s/fdef keywords->js
  :args (s/cat :x any?)
  :ret any?)

(defn- keywords->js [x]
  (cond
    (coll? x) (walk keywords->js identity x)
    (keyword? x) (-> x name capitalization-preserving-camel-case
                     (string/replace #"\?" ""))
    :else x))

(s/fdef keywords->clj
  :args (s/cat :x any?)
  :ret any?)

(defn- keywords->clj [x]
  (cond
    (coll? x) (walk keywords->clj identity x)
    (keyword? x) (-> x name ->kebab-case keyword)
    :else x))

(s/fdef ->js
  :args (s/cat :x any?)
  :ret any?)

(defn ->js [x]
  (clj->js (walk keywords->js identity x)))

(s/fdef ->clj
   :args (s/cat :x any?)
   :ret any?)

(defn ->clj [x]
  (walk keywords->clj identity (js->clj x :keywordize-keys true)))
