package v3

import TailWindClasses
import TailWindClasses.submitButton
import dev.fritz2.core.RenderContext
import dev.fritz2.headless.components.inputField
import koin
import lineUp
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import stackUp

/**
 * Replace the handler by one that talks to a server
 * and add koin for dependency injection.
 */
fun RenderContext.v3TalkToOurServer() {
    // we are not cavemen, let's use dependency injection
    // koin does not like being started multiple times like we do in this demo
    GlobalContext.stopKoin()
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
    // no changes here
    val searchResultStore by koin.inject<SearchResultStore>()
    val queryTextStore by koin.inject<QueryTextStore>()
    lineUp {
        inputField(TailWindClasses.defaultSpaceX) {
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
                pre {
                    +results
                }
            } else {
                p {
                    +"Nothing yet"
                }
            }
        }
}



