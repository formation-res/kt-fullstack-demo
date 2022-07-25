
package v5

import TWClasses.submitButton
import dev.fritz2.core.*
import dev.fritz2.headless.components.inputField
import lineUp
import org.koin.core.context.startKoin
import org.w3c.dom.HTMLUListElement
import recipesearch.Recipe
import stackUp

fun RenderContext.v5UseKtSearch() {
    startKoin {
        modules(searchModule)
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
        inputField(TWClasses.defaultSpaceX) {
            value(queryTextStore)
            type("text")
            placeholder("cheese")
            label { +"Query" }
            inputTextfield {  }
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
                    "Found ${results.totalHits} recipes!"
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



