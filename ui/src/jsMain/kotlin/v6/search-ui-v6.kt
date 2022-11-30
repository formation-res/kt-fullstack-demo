package v6

import TailWindClasses
import TailWindClasses.submitButton
import com.tryformation.localization.Translatable
import dev.fritz2.core.*
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.components.listbox
import koin
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.filter
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


/**
 * Add fluent-js via fluent-kotlin to add translations for different languages.
 */
@OptIn(DelicateCoroutinesApi::class)
fun RenderContext.v6AddTranslations() {
    GlobalContext.stopKoin()
    GlobalScope.launch {
        startKoin {
            modules(v6Module)
        }
        // little hack to get this to load in a co-routine scope because resource loading is suspending
        koin.declare(TranslationStore.load(fallback = "en-GB"))

        searchUi()
    }
}

/**
 * We don't want to use String literals, so use enums for your language string identifiers
 */
enum class UiTexts : Translatable {
    Title,
    Cheese,
    Query,
    SearchButton,
    EmptySearch,
    FoundResults
    ;

    // language string ids are prefixed and snake cases
    override val prefix: String = "demo"
}

private fun RenderContext.searchUi() {
    val translationStore by koin.inject<TranslationStore>()
    div("flex flex-col justify-between h-full") {
        div("container mx-auto font-sans") {
            // components are extension functions
            stackUp {
                h1("text-2xl center") {
                    // the translation is a Flow<String>
                    // we can react to changes if we switch language
                    translationStore[UiTexts.Title].renderText(this)
                }
                searchForm()
                searchResults()
                // obviously not a nice UX, exercise for the reader: make this nicer ;-)
            }
        }
        lineUp() {
            button {
                +"Dutch"
                // we map the click event to the locale id we want to set
                clicks.map { Locales.NL_NL.id } handledBy translationStore.updateLocale
            }
            button {
                +"British English"
                clicks.map { Locales.EN_GB.id } handledBy translationStore.updateLocale
            }
            button {
                +"US English"
                clicks.map { Locales.EN_US.id } handledBy translationStore.updateLocale
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
            inputField(TailWindClasses.defaultSpaceX) {
                value(queryTextStore)
                label { translationStore[UiTexts.Query].renderText(this) }
                inputTextfield {
                    type("text")
                    placeholder(tl)
                    keyups.filter {
                        it.keyCode == 13
                    } handledBy searchResultStore.search
                }
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



