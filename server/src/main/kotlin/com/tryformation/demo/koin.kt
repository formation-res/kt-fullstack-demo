package com.tryformation.demo

import com.jillesvangurp.ktsearch.KtorRestClient
import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.repository.repository
import org.koin.dsl.module
import recipesearch.Recipe
import recipesearch.RecipeSearch

val searchModule = module {
    single { SearchClient(
        KtorRestClient(host = "localhost", port = 9200)
    ) }
    single {
        val client = get<SearchClient>()
        client.repository(indexName = "recipes", serializer = Recipe.serializer())
    }
    single { RecipeSearch(repository = get(), searchClient = get()) }
}