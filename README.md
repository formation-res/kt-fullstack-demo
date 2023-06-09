# Demo Project

This demo was created to support presentations at the Kotlin Virtual User group, the Opensearch community, and the ELasticsearch Meetups. You can watch the full presentation here:
https://www.youtube.com/watch?v=c6wcpM3EuRg&t=1s

This demo project implements a UI and server for a little recipe search engine to demonstrate a few things:

1. How awesome multi platform Kotlin is and how you can use it as a Fullstack platform. It's used for both frontend (Browser/JS) and backend code (JVM)
1. Our **[kt-search](https://github.com/jillesvangurp/kt-search)** client for Opensearch and Elasticsearch. This was developed over many years by FORMATION CTO Jilles and we use this for our main app as well. This demoes a few of its features both running both on the JVM (ktor) and in a kotlin web UI via kotlin-js.
1. How to build frontend code with Kotlin and how to structure it properly.
1. How to integrate both kotlin multi platform libraries and javascript npms in Kotlin-js
1. How to use our [fluent-kotlin](https://github.com/formation-res/fluent-kotlin) to localize your fritz2 or other kotlin code.

The project has three kotlin modules:

- `lib`: this is a small multi platform kotlin library that implements recipe search using [kt-search](https://github.com/jillesvangurp/kt-search) that is used in both server and ui
- `server`: a small ktor server that implements a REST API
- `ui`: a [Fritz2](https://www.fritz2.dev/) based web application with several versions of the search UI. v4 uses ktor v5 and v6 embeds the kotlin-js version of lib and talk to elasticsearch directly. v6 adds translations via our [fluent-kotlin](https://github.com/formation-res/fluent-kotlin) project

## Getting started

1. Run elasticsearch (and Kibana) using the provided docker-compose file `docker-compose up -d`. After it starts you can access [Kibana](http://localhost:5601), if you want to poke around in your Elasticsearch setup (optional).
1. Start the server main method from your IDE, it will run on port 9090
1. Once the server is running, you can bootstrap the recipe search with content: `curl -XPOST localhost:9090/bootstrap`. This will create the recipe search engine with a few recipes.
1. Start the UI development server, it will run on port [8080](http://localhost:8080/): `./gradlew :ui:jsBrowserDevelopmentRun --continuous` - note, I disabled auto reloading via some overrides in `ui/webpack.config.d/devServer.config.js` This is to avoid the double reload it seems to trigger on every change. Simple reload manually with `ctrl/cmd + r` in your browser.

The `App.kt` file in the ui module has several versions of the UI with increasing complexity. 
