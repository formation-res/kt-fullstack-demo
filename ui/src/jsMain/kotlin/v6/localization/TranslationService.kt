@file:Suppress("unused")

package v6.localization

import com.tryformation.fluent.BundleSequence
import com.tryformation.fluent.translate
import com.tryformation.localization.Translatable
import dev.fritz2.core.RootStore
import dev.fritz2.core.storeOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.js.Json

class TranslationService(
    bundleSequence: BundleSequence,
    private val defaultLanguage: String = Locales.EN_GB.id
) : RootStore<BundleSequence>(bundleSequence) {
    private val languageCodeStore = storeOf(Locales.NL_NL.id)

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
            LocalizationUtil.loadBundleSequence(listOf(locale.id), defaultLanguage)
        } else {
            old
        }
    }

    private val setLocale = handle<Locales?> { current, locale ->
        if (locale != null) {
            LocalizationUtil.loadBundleSequence(listOf(locale.id),defaultLanguage)
        } else {
            current
        }
    }

    init {
        languageCodeStore.data.map { it.let { code -> Locales.getByIdOrNull(code) } } handledBy setLocale
    }
}