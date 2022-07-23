# Demo Project

This demo was created to support presentations at the Kotlin Virtual User group as well as the Opensearch community meeting.

The project implements a little recipe search engine to demonstrate a few things:

1. How awesome multi platform Kotlin is and how you can use it as a Fullstack platform.
2. My kt-search client for Opensearch and Elasticsearch
3. How build frontend code with Kotlin and how to structure it properly
4. How to integrate both kotlin multiplatform libraries and javascript npms in Kotlin-js

It has three kotlin modules:

- lib: this is a small multi platform kotlin library that is used in both server and ui
- server: a small ktor server that implements a REST API
- ui: a Fritz2 based web application

## Getting started

- run elasticsearch (and Kibana) using the provided docker-compose file `docker-compose up -d`. After it starts you can access [Kibana](http://localhostL5601)
- start the server main method from your IDE, it will run on port 9090
- start the UI server, it will run on port [8080](http://localhost:8080/): `./gradlew :ui:jsBrowserDevelopmentRun --continuous` - note, I disabled auto reloading via some overrides in `ui/webpack.config.d/devServer.config.js` This is to avoid the double reload it seems to trigger on every change. Simple reload manually with `ctrl/cmd + r`.



