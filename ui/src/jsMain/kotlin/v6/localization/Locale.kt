package v6.localization

enum class LocaleNames : Translatable {
    EN_GB,
    NL_NL,
    ;

    override val prefix: String = "locale"
}

enum class Locale(
    val id: String,
    val translatable: Translatable,
    vararg val aliases: String,
) {
    EN_GB("en-GB", LocaleNames.EN_GB, "en"),
    NL_NL("nl-NL", LocaleNames.NL_NL, "nl"),
    ;

    companion object {
        fun getByIdOrNull(id: String): Locale? {
            val values = values()
            return values.firstOrNull { it.id == id }
                ?: values.firstOrNull { id in it.aliases }
        }
    }
}