package v6

import TWClasses
import TWClasses.submitButton
import dev.fritz2.core.*
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.components.listbox
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import lineUp
import org.koin.core.context.GlobalContext
import v6.localization.LocalizationUtil
import v6.localization.Translation
import org.koin.core.context.startKoin
import org.w3c.dom.HTMLUListElement
import recipesearch.Recipe
import stackUp
import v6.localization.Locale
import v6.localization.Translatable
import kotlin.js.json

enum class UiTexts : Translatable {
    Title,
    Cheese,
    Query,
    SearchButton,
    EmptySearch,
    FoundResults
    ;

    override val prefix: String = "demo"
}

@OptIn(DelicateCoroutinesApi::class)
fun RenderContext.v6AddTranslations() {
    GlobalContext.stopKoin()
    GlobalScope.launch {
        startKoin {
            modules(searchModule)
        }
        // little hack to get this to load in a co-routine scope because resource loading is suspending
        koin.declare(LocalizationUtil.load())

        searchUi()

    }
}

private fun RenderContext.searchUi() {
    val translation by koin.inject<Translation>()
    div("container mx-auto font-sans") {
        // components are extension functions
        stackUp {
            h1("text-2xl center") {
                translation[UiTexts.Title].renderText(this)
            }
            searchForm()
            searchResults()
            lineUp {
                button {
                    +"NL"
                    clicks.map { "nl-NL" } handledBy translation.updateLocale
                }
                button {
                    +"EN"
                    clicks.map { "en-GB" } handledBy translation.updateLocale
                }
            }
        }
    }
}

private fun RenderContext.searchForm() {
    val searchResultStore by koin.inject<SearchResultStore>()
    val queryTextStore by koin.inject<QueryTextStore>()
    val translation by koin.inject<Translation>()

    lineUp {
        // placeholder takes a string, not a flow so we render the flow of translations
        translation[UiTexts.Cheese].render { tl ->
            inputField(TWClasses.defaultSpaceX) {
                value(queryTextStore)
                type("text")
                placeholder(tl)
                label { translation[UiTexts.Query].renderText(this) }
                inputTextfield { }
            }
        }
        button(submitButton) {
            translation[UiTexts.SearchButton].renderText(this)
            clicks handledBy searchResultStore.search
        }
    }
}

private fun RenderContext.searchResults() {
    val translation by koin.inject<Translation>()

    val searchResultStore by koin.inject<SearchResultStore>()
    searchResultStore.data.render { results ->
        if (results != null) {
            p {
                translation[UiTexts.FoundResults, json("amount" to results.totalHits.toString())].renderText(this)
            }
            ul {
                results.items.forEach {
                    recipeResultComponent(it)
                }
            }

        } else {
            p {
                translation[UiTexts.EmptySearch].renderText(this)
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



