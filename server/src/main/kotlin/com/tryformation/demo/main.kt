@file:Suppress("unused")

package com.tryformation.demo

import com.jillesvangurp.ktsearch.asBucketAggregationResult
import com.jillesvangurp.ktsearch.index
import com.jillesvangurp.ktsearch.repository.IndexRepository
import com.jillesvangurp.searchdsls.querydsl.TermsAgg
import com.jillesvangurp.searchdsls.querydsl.agg
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.koin.core.logger.Level
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import recipesearch.Recipe
import recipesearch.RecipeSearch

fun main(args: Array<String>) {
    // start ktor, configuration lives in application.conf
    EngineMain.main(args)
}

fun Application.module() {
    routing {
        route("/") {
            get {
                call.respond("Hi there!")
            }
        }
        route("/hi") {
            get {
                call.respond("Hi back!")
            }
        }
        searchRoutes()
    }

    install(ContentNegotiation) {
        json(
            PRETTY_JSON
        )
    }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
    install(Koin) {
        slf4jLogger(level = Level.DEBUG)
        modules(searchModule)
    }
}

fun Routing.searchRoutes() {
    val recipeSearch by inject<RecipeSearch>()
    val repository by inject<IndexRepository<Recipe>>()
    val logger = KotlinLogging.logger { }

    get("/search") {
        val query = this.context.request.queryParameters["q"] ?: ""
        val from = this.context.request.queryParameters["from"]?.toIntOrNull() ?: 0
        val size = this.context.request.queryParameters["size"]?.toIntOrNull() ?: 10
        call.respond(recipeSearch.search(text = query, start = from, hits = size)).also {
            logger.info { "Searched for '$query'" }
        }
    }

    get("/complete") {
        val query = this.context.request.queryParameters["q"] ?: ""
        val from = this.context.request.queryParameters["from"]?.toIntOrNull() ?: 0
        val size = this.context.request.queryParameters["size"]?.toIntOrNull() ?: 10
        call.respond(recipeSearch.autocomplete(text = query, start = from, hits = size))
    }

    post("/bootstrap") {
        repository.deleteIndex()
        logger.info { "deleted index" }
        recipeSearch.createNewIndex()
        logger.info { "created new index" }
        repository.bulk(bulkSize = 3) {
            val contextClassLoader = Thread.currentThread().contextClassLoader
            // we use blocking file IO here
            withContext(Dispatchers.IO) {
                contextClassLoader
                    .getResourceAsStream("recipes")
                    ?.readAllBytes()
                    ?.decodeToString()
                    ?.lines()
                    ?.filter { it.endsWith(".json") }
                    ?.forEach { file ->
                        logger.info { "indexing $file" }
                        val json = contextClassLoader.getResourceAsStream("recipes/$file")?.readAllBytes()
                            ?.decodeToString() ?: error("$file not found")
                        val recipe = DEFAULT_JSON.decodeFromString<Recipe>(json)
                        logger.info { json }
                        // re-serialize without pretty printing
                        index(recipe, id = file)
                    }
            }
        }
        call.respond(HttpStatusCode.OK)
    }

    get("/aggs") {
        val response = repository.search {
            agg("by_tag", TermsAgg(Recipe::tags))
        }
        call.respond(response.aggregations["by_tag"]?.asBucketAggregationResult()?:error("oops"))
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

@OptIn(ExperimentalSerializationApi::class)
val PRETTY_JSON: Json = Json {
    // don't rely on external systems being written in kotlin or even having a language with default values
    // the default of false is insane and dangerous
    encodeDefaults = true
    // save space
    prettyPrint = true
    // people adding shit to the json is OK, we're forward compatible and will just ignore it
    isLenient = true
    // encoding nulls is meaningless and a waste of space.
    explicitNulls = false
    // adding object keys is OK even if older clients won't understand it
    ignoreUnknownKeys = true
    // make sure new enum values don't break deserialization (will be null)
    coerceInputValues = true
}