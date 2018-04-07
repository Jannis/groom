(require '[cljs.build.api :as b]
         '[cljs.repl]
         '[cljs.repl.browser])

(b/build "src"
  {:output-dir "./public/js"
   :output-to "./public/js/index.js"
   :asset-path "./js"
   :optimizations :none
   :verbose true
   :compiler-stats true
   :main 'starwars
   :install-deps true
   :package-json-resolution ["browser" "main"]
   :infer-externs true
   :closure-defines '{process.env/NODE_ENV "production"}
   :npm-deps {:iterall "1.2.2"
              :apollo-client "2.2.7"
              :apollo-link-http "1.5.3"
              :apollo-link-state "0.4.1"
              :apollo-cache-inmemory "1.1.11"
              :graphql "0.13.1"
              :graphql-tag "2.8.0"
              :react "16.2.0"
              :react-dom "16.2.0"
              :react-apollo "2.0.4"}})

(cljs.repl/repl (cljs.repl.browser/repl-env)
  :watch "."
  :output-dir "out")
