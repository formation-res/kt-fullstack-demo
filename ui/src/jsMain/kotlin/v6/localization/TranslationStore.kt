@file:Suppress("unused")

package v6.localization

import com.tryformation.fluent.BundleSequence
import com.tryformation.fluent.FluentBundle
import com.tryformation.fluent.FluentResource
import com.tryformation.fluent.translate
import com.tryformation.localization.Translatable
import dev.fritz2.core.RootStore
import dev.fritz2.core.storeOf
import dev.fritz2.remote.http
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.js.Json

class TranslationStore(
    bundleSequence: BundleSequence,
    private val defaultLanguage: String = Locales.NL_NL.id
) : RootStore<BundleSequence>(bundleSequence) {
    private val languageCodeStore = storeOf(defaultLanguage)

    operator fun get(translatable: Translatable, json: Json? = null): Flow<String> {
        return data.map { it.translate(translatable.messageId, json) }
    }

    operator fun get(translatable: Translatable, jsonFlow: Flow<Json>): Flow<String> {
        return data.combine(jsonFlow) { bundleSeq, json -> bundleSeq.translate(translatable.messageId, json) }
    }

    fun getString(translatable: Translatable, json: Json? = null): String {
        return current.translate(translatable.messageId, json)
    }

    val updateLocale = handle<String> { old, code ->
        val locale = Locales.getByIdOrNull(code)
        if (locale != null) {
            loadBundleSequence(listOf(locale.id), defaultLanguage)
        } else {
            old
        }
    }

    private val setLocale = handle<Locales?> { current, locale ->
        if (locale != null) {
            loadBundleSequence(listOf(locale.id),defaultLanguage)
        } else {
            current
        }
    }

    init {
        languageCodeStore.data.map { it.let { code -> Locales.getByIdOrNull(code) } } handledBy setLocale
    }

    companion object {
        suspend fun load(fallback: String): TranslationStore {
            val languages = (window.navigator.language.let { listOf(it) } + window.navigator.languages.toList()).distinct()
            console.log("browser languages: ${languages.joinToString(",")}")

            val best = languages.firstOrNull {
                Locales.getByIdOrNull(it) != null
            }
            return TranslationStore(loadBundleSequence(listOfNotNull(best), fallback))
        }

        private val baseUrl = window.location.let { l ->
            if (l.protocol == "file:") {
                "https://app.tryformation.com/lang"
            } else {
                l.protocol + "//" + l.host + "/lang"
            }
        }

        suspend fun loadBundleSequence(locales: List<String>, fallback: String?): BundleSequence {
            return if (fallback != null) {
//            console.log("loading locales: $locales and fallback: $fallback")
                (locales + fallback).distinct().mapNotNull { locale ->
                    loadBundle(locale)
                }.toTypedArray()
            } else {
                locales.map { loadBundle(it) ?: error("$it not found") }.toTypedArray()
            }
        }

        suspend fun loadBundle(locale: String): FluentBundle? {
            val url = "$baseUrl/${locale}.ftl"
            return try {
                val ftlContent = http(url).get().body()

                val resource = FluentResource(ftlContent)
                val bundle = FluentBundle(locale)
                val errors = bundle.addResource(resource)
                if (errors.isNotEmpty()) {
                    // Syntax errors are per-message and don't break the whole resource
                    errors.forEach {
                        console.error(locale, it)
                    }
                }
                bundle
            } catch (e: Exception) {
                null
            }
        }
    }
}