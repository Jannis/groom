(require '[cljs.build.api :as b]
         '[clojure.core.async :refer [go]]
         '[ring.adapter.jetty :refer [run-jetty]]
         '[ring.middleware.file :refer [wrap-file]]
         '[ring.middleware.content-type :refer [wrap-content-type]]
         '[ring.middleware.not-modified :refer [wrap-not-modified]])

(go
 (run-jetty (-> (constantly {:status 404})
                (wrap-file "./public")
                (wrap-content-type)
                (wrap-not-modified))
            {:port 8002}))

(b/watch "src"
  {:output-dir "./public/js"
   :output-to "./public/js/index.js"
   :asset-path "./js"
   :optimizations :none
   :verbose true
   :compiler-stats true
   :main 'starwars
   :install-deps true
   :pseudo-names false
   :package-json-resolution ["browser" "main"]
   :infer-externs true
   :closure-defines '{process.env/NODE_ENV "production"}
   :npm-deps {:iterall "1.2.2"
              :apollo-client "2.2.7"
              :apollo-link-http "1.5.3"
              :apollo-link-state "0.4.1"
              :apollo-cache-inmemory "1.1.11"
              :symbol-observable "1.0.2"
              :graphql "0.13.1"
              :graphql-tag "2.8.0"
              :react "16.2.0"
              :react-dom "16.2.0"
              :react-apollo "2.0.4"}})
