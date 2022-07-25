@file:Suppress("unused")

package v5

import com.jillesvangurp.ktsearch.KtorRestClient
import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.repository.repository
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import recipesearch.Recipe
import recipesearch.RecipeSearch

val searchModule = module {
    single { QueryTextStore() }
    single { SearchResultStore() }
    single { SearchClient(
        KtorRestClient(host = "localhost", port = 9200)
    ) }
    single {
        val client = get<SearchClient>()
        client.repository(indexName = "recipes", serializer = Recipe.serializer())
    }
    single { RecipeSearch(repository = get(), searchClient = get()) }
}

val  RenderContext.koin get() = GlobalContext.get()
val  RootStore<*>.koin get() = GlobalContext.get()