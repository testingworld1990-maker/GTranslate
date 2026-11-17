package com.tanglishvideotranslator.settings

data class AppSettings(
    val geminiApiKey: String = "",
    val userConsentGiven: Boolean = false,
    val tanglishStyle: String = "casual",
    val fontSize: Float = 24f,
    val backgroundOpacity: Float = 0.35f,
    val subtitlePosition: String = "bottom",
    val dualDisplay: Boolean = false,
    val syncOffsetMs: Int = 0,
)
