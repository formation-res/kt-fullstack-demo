package v6.localization

import com.tryformation.localization.Locale
import com.tryformation.localization.Translatable

enum class LocaleNames : Translatable {
    EN_GB,
    NL_NL,
    ;

    override val prefix: String = "locale"
}

enum class Locales(
    override val id: String,
    override val translatable: Translatable,
    override val aliases: Array<String>,
): Locale {
    EN_GB("en-GB", LocaleNames.EN_GB, arrayOf("en-US","en")),
    NL_NL("nl-NL", LocaleNames.NL_NL, arrayOf("nl", "nl-BE")),
    ;

    companion object {
        fun getByIdOrNull(id: String): Locales? {
            val values = values()
            return values.firstOrNull { it.id == id }
                ?: values.firstOrNull { id in it.aliases }
        }
    }
}