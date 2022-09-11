@file:Suppress("unused")

package v6.localization

import com.tryformation.localization.LocalizedTranslationBundleSequence
import com.tryformation.localization.LocalizedTranslationBundleSequenceProvider
import com.tryformation.localization.Translatable
import dev.fritz2.core.RootStore
import dev.fritz2.core.storeOf
import dev.fritz2.remote.http
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class TranslationStore(
    bundleSequence: LocalizedTranslationBundleSequence,
    private val defaultLanguage: String = Locales.NL_NL.id
) : RootStore<LocalizedTranslationBundleSequence>(bundleSequence) {
    private val languageCodeStore = storeOf(defaultLanguage)

    operator fun get(translatable: Translatable, json: Map<String, Any>? = null): Flow<String> {
        return data.map { bundleSequence ->
            bundleSequence.translate(translatable, args = json)
        }.map { it.message }
    }

    operator fun get(translatable: Translatable, jsonFlow: Flow<Map<String, Any>>): Flow<String> {
        return data.combine(jsonFlow) { bundleSeq, json ->
            bundleSeq.translate(translatable, args = json).message
        }
    }

    fun getString(translatable: Translatable, json: Map<String, Any>? = null): String {
        return current.translate(translatable, args = json).message
    }

    val updateLocale = handle<String> { _, newLocale ->
        provider.loadBundleSequence(listOf(newLocale), defaultLanguage, ::fetchFtl)
    }

    private val setLocale = handle<Locales?> { current, locale ->
        if (locale != null) {
            provider.loadBundleSequence(listOf(locale.id), defaultLanguage, ::fetchFtl)
        } else {
            current
        }
    }

    init {
        languageCodeStore.data.map { it.let { code -> Locales.getByIdOrNull(code) } } handledBy setLocale
    }

    companion object {
        internal val provider = LocalizedTranslationBundleSequenceProvider()

        private val baseUrl = window.location.let { l ->
            if (l.protocol == "file:") {
                "https://app.tryformation.com/lang"
            } else {
                l.protocol + "//" + l.host + "/lang"
            }
        }

        private suspend fun fetchFtl(newLocale: String) = try {
            http("${baseUrl}/${newLocale}.ftl").get().body()
        } catch (e: Exception) {
            null
        }

        suspend fun load(fallback: String): TranslationStore {
            val languages =
                (window.navigator.language.let { listOf(it) } + window.navigator.languages.toList()).distinct()
            console.log("browser languages: ${languages.joinToString(",")}")

            val best = languages.firstOrNull {
                Locales.getByIdOrNull(it) != null
            }
            return TranslationStore(provider.loadBundleSequence(listOfNotNull(best), fallback, ::fetchFtl))
        }
    }

}