package v6

import dev.fritz2.core.RootStore
import koin
import kotlinx.coroutines.Job
import recipesearch.Recipe
import recipesearch.RecipeSearch
import recipesearch.SearchResults

class QueryTextStore: RootStore<String>("", Job())

class SearchResultStore : RootStore<SearchResults<Recipe>?>(null, Job()) {
    val queryTextStore by koin.inject<QueryTextStore>()
    val recipeSearch by koin.inject<RecipeSearch>()

    val search = handle {
        recipeSearch.search(queryTextStore.current,0,20)
    }
}