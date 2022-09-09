package v6.localization

import com.tryformation.localization.Locale

enum class Locales(
    override val languageCode: String,
    override val countryCode: String?,
    override val aliases: Array<String>,
) : Locale {
    EN_GB("en", "GB", arrayOf("en-US", "en")),
    NL_NL("nl", "NL", arrayOf("nl", "nl-BE")),
    ;

    override val prefix = "locales"

    companion object {
        fun getByIdOrNull(id: String): Locales? {
            val values = values()
            return values.firstOrNull { it.id == id }
                ?: values.firstOrNull { id in it.aliases }
        }
    }
}