# Demo Project

This demo was created to support presentations at the Kotlin Virtual User group as well as the Opensearch community meeting.

The project implements a little recipe search engine to demonstrate a few things:

1. How awesome multi platform Kotlin is and how you can use it as a Fullstack platform.
1. Our **[kt-search](https://github.com/jillesvangurp/kt-search)** client for Opensearch and Elasticsearch. This was developed over many years by FORMATION CTO Jilles and we use this for our main app as well.
1. How build frontend code with Kotlin and how to structure it properly
1. How to integrate both kotlin multi platform libraries and javascript npms in Kotlin-js

The project has three kotlin modules:

- `lib`: this is a small multi platform kotlin library that is used in both server and ui
- `server`: a small ktor server that implements a REST API
- `ui`: a [Fritz2](https://www.fritz2.dev/) based web application

## Getting started

1. Run elasticsearch (and Kibana) using the provided docker-compose file `docker-compose up -d`. After it starts you can access [Kibana](http://localhostL5601), if you want to poke around in your Elasticsearch setup (optional).
1. Start the server main method from your IDE, it will run on port 9090
1. Once the server is running, you can bootstrap the recipe search with content: `curl -XPOST localhost:9090/bootstrap`. This will creat the recipe search engine with a few recipes.
1. Start the UI development server, it will run on port [8080](http://localhost:8080/): `./gradlew :ui:jsBrowserDevelopmentRun --continuous` - note, I disabled auto reloading via some overrides in `ui/webpack.config.d/devServer.config.js` This is to avoid the double reload it seems to trigger on every change. Simple reload manually with `ctrl/cmd + r` in your browser.

The App.kt file in the ui module has several versions of the UI with increasing complexity. Simply, keep all but 1 commented out and wait for gradle to recompile.
