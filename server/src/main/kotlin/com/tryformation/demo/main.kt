@file:Suppress("unused")

package com.tryformation.demo

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {

    // start ktor
    EngineMain.main(args)
}

// referenced in application.conf
fun Application.module() {
    install(ContentNegotiation) {
        json(
            DEFAULT_JSON
        )
    }
    install(CORS) {
        allowHost("0.0.0.0:9090")
    }
    routing {
        route("/") {
            get {
                call.respond("hi!")
            }
        }
    }
}

// kotlinx serialization defaults are actively harmful
@OptIn(ExperimentalSerializationApi::class)
val DEFAULT_JSON: Json = Json {
    // don't rely on external systems being written in kotlin or even having a language with default values
    // the default of false is insane and dangerous
    encodeDefaults = true
    // save space
    prettyPrint = false
    // people adding shit to the json is OK, we're forward compatible and will just ignore it
    isLenient = true
    // encoding nulls is meaningless and a waste of space.
    explicitNulls = false
    // adding object keys is OK even if older clients won't understand it
    ignoreUnknownKeys = true
    // make sure new enum values don't break deserialization (will be null)
    coerceInputValues = true
}