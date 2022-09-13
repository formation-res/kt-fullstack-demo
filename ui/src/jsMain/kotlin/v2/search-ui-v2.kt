package v2
import TailWindClasses
import TailWindClasses.submitButton
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.storeOf
import dev.fritz2.headless.components.inputField
import kotlinx.coroutines.flow.filter
import lineUp
import stackUp

/**
 * Adds styling and stores.
 */
fun RenderContext.v2ComponentsAndStores() {
    // OK, let's make this less ugly
    div("container mx-auto font-sans") {
        // components are extension functions
        stackUp {
            h1("text-2xl center") {
                +"Recipe Search"
            }
            searchForm()
            searchResults()
        }
    }

}

private fun RenderContext.searchForm() {
    lineUp {
        inputField(TailWindClasses.defaultSpaceX) {
            // input values are stored in a store
            value(SearchFieldStore)
            type("text")
            placeholder("cheese")
            label { +"Query" }
            inputTextfield {
                keyups.filter {
                    it.keyCode == 13
                } handledBy SearchFieldStore.search
            }
        }
        button(submitButton) {
            +"Search"
            // the store has handlers to process events
            clicks handledBy SearchFieldStore.search
        }
    }
}

private fun RenderContext.searchResults() {
    ul {
        resultStore.data.render {results ->
            results.forEach {
                li {
                    +it
                }
            }
        }
    }
}

// fritz 2 has a concept of stores for managing state
// store == Flow<T>
// It's Kotlin, so we have types and generics ..

// this where search results will live, assume we have strings for now ...
val resultStore = storeOf(listOf<String>())

// our search field also has state but we also need behavior
object SearchFieldStore: RootStore<String>("") {
    // when you click the button, it is handled here
    val search = handle { query ->
        if(query.isNotBlank()) {
            // this is a suspending block so we can do IO
            // for now just append the text to the result list
            resultStore.update(resultStore.current + query)
        }
        // and clear the field
        ""
    }
}


