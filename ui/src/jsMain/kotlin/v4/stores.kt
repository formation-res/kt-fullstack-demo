package v4

import com.jillesvangurp.ktsearch.DEFAULT_JSON
import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.routing.encodeURIComponent
import koin
import kotlinx.coroutines.Job
import recipesearch.Recipe
import recipesearch.SearchResults

class QueryTextStore: RootStore<String>("", Job())

class SearchResultStore : RootStore<SearchResults<Recipe>?>(null, Job()) {
    val queryTextStore by koin.inject<QueryTextStore>()

    val search = handle {
        // note we use browser fetch here; ktor client would be a good alternative to this
        http("http://localhost:9090/search?q=${encodeURIComponent(queryTextStore.current)}")
            .acceptJson()
            .contentType("application/json").get().body().let {
                // use kotlinx serialization to parse the response body
                DEFAULT_JSON.decodeFromString(SearchResults.serializer(Recipe.serializer()), it)
            }

    }
}