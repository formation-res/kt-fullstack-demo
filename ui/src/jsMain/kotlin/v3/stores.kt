package v3

import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.routing.encodeURIComponent
import koin

class QueryTextStore: RootStore<String>("")

class SearchResultStore : RootStore<String?>(null) {
    val queryTextStore by koin.inject<QueryTextStore>()

    val search = handle {
        // updates the response as the new store value
        http("http://localhost:9090/search?q=${encodeURIComponent(queryTextStore.current)}")
            .acceptJson()
            .contentType("application/json").get().body()
    }
}