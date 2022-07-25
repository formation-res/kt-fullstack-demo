package v4

import com.jillesvangurp.ktsearch.DEFAULT_JSON
import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.routing.encodeURIComponent
import recipesearch.Recipe
import recipesearch.SearchResults

class QueryTextStore: RootStore<String>("")

class SearchResultStore : RootStore<SearchResults<Recipe>?>(null) {
    val queryTextStore by koin.inject<QueryTextStore>()

    val search = handle {
        http("http://localhost:9090/search?q=${encodeURIComponent(queryTextStore.current)}")
            .acceptJson()
            .contentType("application/json").get().body().let {
                DEFAULT_JSON.decodeFromString(SearchResults.serializer(Recipe.serializer()), it)
            }

    }
}