
package v5

import TailWindClasses
import TailWindClasses.submitButton
import dev.fritz2.core.*
import dev.fritz2.headless.components.inputField
import koin
import kotlinx.coroutines.flow.filter
import lineUp
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.w3c.dom.HTMLUListElement
import recipesearch.Recipe
import stackUp

/**
 * Replace our serve with the multiplatform library.
 *
 * Call Elasticsearch directly from the browser.
 */
fun RenderContext.v5UseKtSearch() {
    GlobalContext.stopKoin()
    startKoin {
        modules(v5Module)
    }
    // OK, let's make this less ugly
    div("container mx-auto font-sans") {
        // components are extension functions
        stackUp {
            h1("text-2xl center") { +"Recipe Search" }
            searchForm()
            searchResults()
        }
    }
}

private fun RenderContext.searchForm() {
    val searchResultStore by koin.inject<SearchResultStore>()
    val queryTextStore by koin.inject<QueryTextStore>()
    lineUp {
        inputField(TailWindClasses.defaultSpaceX) {
            value(queryTextStore)
            label { +"Query" }
            inputTextfield {
                type("text")
                placeholder("cheese")
                keyups.filter {
                    it.keyCode == 13
                } handledBy searchResultStore.search
            }
        }
        button(submitButton) {
            +"Search"
            clicks handledBy searchResultStore.search
        }
    }
}

private fun RenderContext.searchResults() {
    val searchResultStore by koin.inject<SearchResultStore>()
        searchResultStore.data.render {results ->
            if(results != null) {
                p {
                    +"Found ${results.totalHits} recipes!"
                }
                div {
                    results.items.forEach {
                        recipeResultComponent(it)
                    }
                }

            } else {
                p {
                    +"Nothing yet"
                }
            }
        }
}

private fun RenderContext.recipeResultComponent(recipe: Recipe) {
    div("max-w-sm rounded overflow-hidden shadow-lg m-2") {
        h3 {
            a("hover:underline") {
                b {
                    +recipe.title
                }
                recipe.sourceUrl?.let { link ->
                    href(link)
                    target("_blank")
                }
            }
        }
        p {
            a("hover:underline") {
                href(recipe.author.url)
                target("_blank")
                +" (${recipe.author.name})"
            }
            +" ${recipe.tags?.joinToString(", ")}"
        }
        h4 {
            +"Ingredients"
        }
        ul("list-disc list-inside") {
            recipe.ingredients.forEach { step ->
                li { +step }
            }
        }
        h4 {
            +"Steps"
        }
        ul("list-disc list-inside") {
            recipe.directions.forEach { step ->
                li { +step }
            }
        }
    }
}




