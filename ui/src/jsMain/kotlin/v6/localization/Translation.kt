@file:Suppress("unused")

package v6.localization

import dev.fritz2.core.RootStore
import dev.fritz2.core.storeOf
import v6.fluent.BundleSequence
import v6.fluent.translate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.js.Json


class Translation(
    bundleSequence: BundleSequence,
) : RootStore<BundleSequence>(bundleSequence) {
    val languageCode = storeOf(Locale.NL_NL.id)

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
        val locale = Locale.getByIdOrNull(code)
        if (locale != null) {
            LocalizationUtil.loadBundleSequence(listOf(locale))
        } else {
            old
        }
    }

    private val setLocale = handle<Locale?> { current, locale ->
        if (locale != null) {
            LocalizationUtil.loadBundleSequence(listOf(locale))
        } else {
            current
        }
    }

    init {
        languageCode.data.map { it.let { code -> Locale.getByIdOrNull(code) } } handledBy setLocale
    }
}