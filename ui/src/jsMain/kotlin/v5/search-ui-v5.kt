
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
                ul {
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

private fun HtmlTag<HTMLUListElement>.recipeResultComponent(it: Recipe) {
    li {
        a("hover:underline") {
            b {
                +it.title
            }
            it.sourceUrl?.let { link ->
                href(link)
                target("_blank")
            }
        }
        a("hover:underline") {
            href(it.author.url)
            target("_blank")
            +" (${it.author.name})"
        }
        +" ${it.tags?.joinToString(", ")}"
    }
}



