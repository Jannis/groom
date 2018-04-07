# WIP: Groom: A React/GraphQL framework for ClojureScript

## Principles

* React for components
* GraphQL for client-side state management
* GraphQL for server queries and mutations
* Component definitions with co-located queries and mutations
* Two-level API:
    - Low-level: Functions for creating components, mutations, queries
    - High-level: Macro-based DSL for creating components, mutations, queries
* Prefer data over code (i.e. Hiccup-style UI definition)
* Encourage spec-driven development

## Stretch Goals

* Server-side rendering
* React Native support out of the box

## Foundations

* React
* Sablono (for Hiccup-style UI definition)
* Apollo (for everything GraphQL)

## Elements

* `groom.graphql`: ClojureScript bindings for everything Apollo and GraphQL
    - `groom.graphql.apollo.*`: ClojureScript bindings for Apollo
    - `groom.graphql.graphql`: ClojureScript bindings for GraphQL
* `groom.resolvers`: Library for defining client-side query and mutation resolvers conveniently
* `groom.component`: Low-level library for defining and rendering components
* `groom.dsl`: High-level macro-based DSL for defining components, queries and mutations

### Specs

* `groom.specs.v1`: Specs for whatever makes sense (**TODO: Work this out in more detail**)
    - `:groom.specs.v1/queries` for Groom's Clojure-style query definitions
    - `:groom.specs.v1/mutations` for Groom's Clojure-style mutation definitions
* `groom.specs.sablono.v1`: Specs for Groom-enhanced Sablono UI definitions
* `groom.specs.graphql.apollo.*`: Specs for Groom's Apollo bindings

## Prerequisites

Groom requires `boot` and `mach` to be installed:
```sh
# boot
sudo bash -c "cd /usr/local/bin && curl -fsSLo boot https://github.com/boot-clj/boot-bin/releases/download/latest/boot.sh && chmod 755 boot"

# mach
sudo npm install -g @juxt/mach
```

## Run Examples

There is currently only one example, demonstrating a few simple components
and basic client-side GraphQL state management. To play with it, run
```sh
boot starwars-example
```

Then open [http://localhost:8002/index.html](http://localhost:8002/index.html) in a browser.

The source code for this example is located in
[examples/starwars/src/starwars.cljs](examples/starwars/src/starwars.cljs).

## License

Groom is licensed under the [MIT License](LICENSE).
