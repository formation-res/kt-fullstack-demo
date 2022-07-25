package v2
import TWClasses.submitButton
import dev.fritz2.core.*
import dev.fritz2.headless.components.inputField
import lineUp
import stackUp

fun RenderContext.v2ComponentsAndStores() {
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
    lineUp {
        inputField(TWClasses.defaultSpaceX) {
            value(SearchFieldStore)
            type("text")
            placeholder("cheese")
            label { +"Query" }
            inputTextfield {  }
        }
        button(submitButton) {
            +"Search"
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
            // for now just append the text to the result list
            resultStore.update(resultStore.current + query)
        }
        // and clear the field
        ""
    }
}


