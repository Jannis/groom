#!/usr/bin/env boot

(def +project+ 'jannis/groom)
(def +version+ "0.1.0-SNAPSHOT")

(set-env!
 :resource-paths #{"examples" "src/main"}
 :dependencies '[[seancorfield/boot-tools-deps "0.4.3" :scope "test"]])

(require '[boot.git :refer [last-commit]]
         '[boot-tools-deps.core :refer [deps]])

(task-options!
 pom  {:project +project+
       :version +version+
       :description "React/GraphQL framework for ClojureScript"
       :url "https://github.com/jannis/groom"
       :scm {:url "https://github.com/jannis/groom"}
       :license {"MIT License" "https://github.com/jannis/groom/tree/master/LICENSE"}}
 push {:repo "deploy-clojars"
       :ensure-branch "master"
       :ensure-clean true
       :ensure-tag (last-commit)
       :ensure-version +version+})

(deftask build-starwars-example
  []
  (with-pre-wrap fs
    (dosh "mach" "starwars-example")
    fs))

(deftask starwars-example
  []
  (comp (deps)
        (build-starwars-example)))

(deftask install-local
  []
  (comp (deps)
        (pom)
        (jar)
        (install)))

(deftask push-snapshot
  []
  (comp (deps)
        (pom)
        (jar)
        (push-snapshot)))

(deftask push-release
  []
  (comp (deps)
        (pom)
        (jar)
        (push-release)))
