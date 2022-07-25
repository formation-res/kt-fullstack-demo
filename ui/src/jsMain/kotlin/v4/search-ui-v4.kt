
package v4

import TWClasses.submitButton
import dev.fritz2.core.*
import dev.fritz2.headless.components.inputField
import lineUp
import org.koin.core.context.startKoin
import stackUp

fun RenderContext.v4UseOurLibrary() {
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
                        li {
                            b {
                                +it.title
                            }
                            +" (${it.author})"
                            +" ${it.tags?.joinToString(", ")}"

                        }
                    }
                }

            } else {
                p {
                    +"Nothing yet"
                }
            }
        }
}



