package v6.localization

import dev.fritz2.remote.http
import v6.fluent.BundleSequence
import v6.fluent.FluentBundle
import v6.fluent.FluentResource
import kotlinx.browser.window

object LocalizationUtil {
    suspend fun load(): Translation {
        val locales: List<Locale> = window.navigator.language.let { langId ->
            Locale.getByIdOrNull(langId)?.let { listOf(it) }
        } ?: window.navigator.languages.mapNotNull { langId ->
            Locale.getByIdOrNull(langId)
        }.takeIf { it.isNotEmpty() }
        ?: emptyList()

        return Translation(loadBundleSequence(locales.distinct()))
    }

    private val baseUrl = window.location.let { l ->
        if (l.protocol == "file:") {
            "https://app.tryformation.com/lang"
        } else {
            l.protocol + "//" + l.host + "/lang"
        }
    }

    suspend fun loadBundleSequence(locales: List<Locale>, fallback: Locale?= Locale.EN_GB): BundleSequence {
        return if(fallback != null) {
            console.log("loading locales: $locales and fallback: $fallback")
            (locales + fallback).distinct().map { locale ->
                loadBundle(locale)
            }.toTypedArray()
        } else {
            locales.map { loadBundle(it) }.toTypedArray()
        }
    }

    suspend fun loadBundle(locale: Locale): FluentBundle {
        val url = "$baseUrl/${locale.id}.ftl"
        val ftlContent = http(url).get().body()

        val resource = FluentResource(ftlContent)
        val bundle = FluentBundle(locale.id)
        val errors = bundle.addResource(resource)
        if (errors.isNotEmpty()) {
            // Syntax errors are per-message and don't break the whole resource
            errors.forEach {
                console.error(it)
            }
        }
        return bundle
    }
}


