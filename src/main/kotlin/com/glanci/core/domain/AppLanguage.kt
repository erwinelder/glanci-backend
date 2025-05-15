package com.glanci.core.domain

sealed class AppLanguage(val langCode: String) {

    data object Czech : AppLanguage("cs")
    data object English : AppLanguage("en")
    data object German : AppLanguage("de")
    data object Russian : AppLanguage("ru")
    data object Spanish : AppLanguage("es")
    data object Ukrainian : AppLanguage("uk")

    companion object {

        fun fromLangCode(langCode: String): AppLanguage? {
            return when (langCode) {
                Czech.langCode -> Czech
                English.langCode -> English
                German.langCode -> German
                Russian.langCode -> Russian
                Spanish.langCode -> Spanish
                Ukrainian.langCode -> Ukrainian
                else -> null
            }
        }

    }

}