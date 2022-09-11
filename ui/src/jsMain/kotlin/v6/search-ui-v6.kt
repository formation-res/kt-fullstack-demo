package v6

import TWClasses
import TWClasses.submitButton
import com.tryformation.localization.Translatable
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.target
import dev.fritz2.headless.components.inputField
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import lineUp
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.w3c.dom.HTMLUListElement
import recipesearch.Recipe
import stackUp
import v6.localization.Locales
import v6.localization.TranslationStore

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
        koin.declare(TranslationStore.load(fallback = "en-GB"))

        searchUi()

    }
}

private fun RenderContext.searchUi() {
    val translationStore by koin.inject<TranslationStore>()
    div("container mx-auto font-sans") {
        // components are extension functions
        stackUp {
            h1("text-2xl center") {
                translationStore[UiTexts.Title].renderText(this)
            }
            searchForm()
            searchResults()
            lineUp {
                button {
                    +"NL"
                    clicks.map { Locales.NL_NL.id } handledBy translationStore.updateLocale
                }
                button {
                    +"EN"
                    clicks.map { Locales.EN_GB.id } handledBy translationStore.updateLocale
                }
            }
        }
    }
}

private fun RenderContext.searchForm() {
    val searchResultStore by koin.inject<SearchResultStore>()
    val queryTextStore by koin.inject<QueryTextStore>()
    val translationStore by koin.inject<TranslationStore>()

    lineUp {
        // placeholder takes a string, not a flow so we render the flow of translations
        translationStore[UiTexts.Cheese].render { tl ->
            inputField(TWClasses.defaultSpaceX) {
                value(queryTextStore)
                type("text")
                placeholder(tl)
                label { translationStore[UiTexts.Query].renderText(this) }
                inputTextfield { }
            }
        }
        button(submitButton) {
            translationStore[UiTexts.SearchButton].renderText(this)
            clicks handledBy searchResultStore.search
        }
    }
}

private fun RenderContext.searchResults() {
    val translationStore by koin.inject<TranslationStore>()

    val searchResultStore by koin.inject<SearchResultStore>()
    searchResultStore.data.render { results ->
        if (results != null) {
            p {
                translationStore[UiTexts.FoundResults, mapOf("amount" to results.totalHits.toString())].renderText(this)
            }
            ul {
                results.items.forEach {
                    recipeResultComponent(it)
                }
            }
        } else {
            p {
                translationStore[UiTexts.EmptySearch].renderText(this)
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



