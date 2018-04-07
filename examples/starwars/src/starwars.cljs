(ns starwars
  (:require [clojure.pprint :refer [pprint]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            [graphql-tag :as gql]
            [groom.component :as c]
            [groom.dsl :refer [defcomponent]]
            [groom.graphql.apollo.cache-inmemory :as cache-inmemory]
            [groom.graphql.apollo.client :refer [apollo-client]]
            [groom.graphql.apollo.link :as link]
            [groom.graphql.apollo.link-http :as link-http]
            [groom.graphql.apollo.link-state :as link-state]
            [groom.resolvers :as resolvers]
            [groom.util.js :as js-util]
            [groom.specs.v1]
            [react :as react]
            [react-apollo :refer [ApolloProvider]]
            [react-dom :as react-dom]))

(enable-console-print!)
(st/instrument)

(def box-style
  {:border "thin solid #eaeaea"
   :display :inline-block
   :padding :1em
   :cursor :pointer
   :margin :0.5em
   :max-width 300})

(defcomponent human
  (queries
    [(human {?id ID!}
       [:id
        :name
        :isSelected +client])])
  (mutations
    [(selectHuman {?id ID!}
       (selectHuman {id ?id} +client [:id]))])
  (render
    (if loading?
      [:div "Loading..."]
      [:div {:on-click selectHuman
             :style (cond-> box-style
                      (:isSelected human) (assoc :background-color "lightGreen"))}
       (:name human)])))

(defcomponent droid
  (queries
    [(droid {?id ID!}
       [:id
        :name
        :isSelected +client])])
  (mutations
    [(selectDroid {?id ID!}
       (selectDroid {id ?id} +client [:id]))])
  (render
    (if loading?
      [:div "Loading..."]
      [:div {:on-click selectDroid
             :style (cond-> box-style
                      (:isSelected droid) (assoc :background-color "orange"))}
       (:name droid)])))

(defcomponent starship
  (queries
    [(starship {?id ID!}
       [:id
        :name
        :length
        :isSelected +client])])
  (mutations
    [(selectShip {?id ID!}
       (selectShip {id ?id} +client [:id]))])
  (render
    (if loading?
      [:div "Loading..."]
      [:div {:on-click selectShip
             :style (cond-> box-style
                      (:isSelected starship) (assoc :background-color "turquoise"))}
       (:name starship) " (Length: " (:length starship) ")"])))

(defcomponent starwars
  (queries
   [(search {?text String!}
      [:__typename
       ... on Character [:id]
       ... on Starship [:id]])])
  (render
    (letfn [(render-one [component item]
              [component {:id (:id item)}])
            (render-items [items typename component]
              (into [] (comp (filter #(= (:__typename %) typename))
                             (map #(render-one component %)))
                    items))]
      [:div
       [:h1 "Starwars: Characters & Starships"]
       [:h2 "Humans"]
       [:div (render-items search "Human" human)]
       [:h2 "Droids"]
       [:div (render-items search "Droid" droid)]
       [:h2 "Starships"]
       [:div (render-items search "Starship" starship)]])))

(def cache
  (let [data (js-util/->clj (gobj/get js/window "__APOLLO_STATE__" {}))]
    (-> (cache-inmemory/in-memory-cache)
        (cache-inmemory/restore data))))

(def http-link
  (link-http/http-link {:uri "https://mpjk0plp9.lp.gql.zone/graphql"}))

(def state-link
  (link-state/with-client-state
    {:cache cache
     :resolvers
     {:Droid {:isSelected (constantly false)}
      :Human {:isSelected (constantly false)}
      :Starship {:isSelected (constantly false)}
      :Mutation {:selectDroid (resolvers/update-field :Droid :isSelected not)
                 :selectHuman (resolvers/update-field :Human :isSelected not)
                 :selectShip (resolvers/update-field :Starship :isSelected not)}}}))

(def client
  (apollo-client
    {:link (link/from [state-link http-link])
     :cache cache
     :connect-to-dev-tools? true}))

(react-dom/render
  (react/createElement ApolloProvider
                       #js {:client client}
                       (react/createElement starwars #js {:text ".*"} nil))
  (gdom/getElement "app"))
