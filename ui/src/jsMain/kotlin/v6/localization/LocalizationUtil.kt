package v6.localization

import com.tryformation.fluent.BundleSequence
import com.tryformation.fluent.FluentBundle
import com.tryformation.fluent.FluentResource
import dev.fritz2.remote.http
import kotlinx.browser.window

object LocalizationUtil {
    suspend fun load(fallback: String = Locales.EN_GB.id): TranslationService {
        val languages = (window.navigator.language.let { listOf(it) } + window.navigator.languages.toList()).distinct()
        console.log("browser languages: ${languages.joinToString(",")}")

        val best = languages.firstOrNull {
            Locales.getByIdOrNull(it) != null
        }
        return TranslationService(loadBundleSequence(listOfNotNull(best), fallback))
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


