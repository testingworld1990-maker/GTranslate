package com.tanglishvideotranslator.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

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

interface SettingsStore {
    val settingsFlow: Flow<AppSettings>
    suspend fun updateSettings(update: (AppSettings) -> AppSettings)
    suspend fun setGeminiApiKey(value: String)
    suspend fun setConsentGranted(value: Boolean)
    suspend fun clearApiKey()
}

class DataStoreSettingsStore(
    private val context: Context,
) : SettingsStore {

    private val securePrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            "secure_app_preferences",
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private val geminiApiKeyKey = stringPreferencesKey("gemini_api_key")
    private val consentKey = booleanPreferencesKey("user_consent_given")
    private val styleKey = stringPreferencesKey("tanglish_style")
    private val fontSizeKey = floatPreferencesKey("font_size")
    private val backgroundOpacityKey = floatPreferencesKey("background_opacity")
    private val positionKey = stringPreferencesKey("subtitle_position")
    private val dualDisplayKey = booleanPreferencesKey("dual_display")
    private val syncOffsetKey = intPreferencesKey("sync_offset_ms")

    override val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            geminiApiKey = securePrefs.getString("gemini_api_key", "") ?: "",
            userConsentGiven = prefs[consentKey] ?: false,
            tanglishStyle = prefs[styleKey] ?: "casual",
            fontSize = prefs[fontSizeKey] ?: 24f,
            backgroundOpacity = prefs[backgroundOpacityKey] ?: 0.35f,
            subtitlePosition = prefs[positionKey] ?: "bottom",
            dualDisplay = prefs[dualDisplayKey] ?: false,
            syncOffsetMs = prefs[syncOffsetKey] ?: 0,
        )
    }

    override suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val current = AppSettings(
                geminiApiKey = securePrefs.getString("gemini_api_key", "") ?: "",
                userConsentGiven = prefs[consentKey] ?: false,
                tanglishStyle = prefs[styleKey] ?: "casual",
                fontSize = prefs[fontSizeKey] ?: 24f,
                backgroundOpacity = prefs[backgroundOpacityKey] ?: 0.35f,
                subtitlePosition = prefs[positionKey] ?: "bottom",
                dualDisplay = prefs[dualDisplayKey] ?: false,
                syncOffsetMs = prefs[syncOffsetKey] ?: 0,
            )
            val next = update(current)

            prefs[consentKey] = next.userConsentGiven
            prefs[styleKey] = next.tanglishStyle
            prefs[fontSizeKey] = next.fontSize
            prefs[backgroundOpacityKey] = next.backgroundOpacity
            prefs[positionKey] = next.subtitlePosition
            prefs[dualDisplayKey] = next.dualDisplay
            prefs[syncOffsetKey] = next.syncOffsetMs

            val editor = securePrefs.edit()
            editor.putString("gemini_api_key", next.geminiApiKey)
            editor.apply()
        }
    }

    override suspend fun setGeminiApiKey(value: String) {
        val editor = securePrefs.edit()
        editor.putString("gemini_api_key", value)
        editor.apply()
    }

    override suspend fun setConsentGranted(value: Boolean) {
        context.dataStore.edit { it[consentKey] = value }
    }

    override suspend fun clearApiKey() {
        val editor = securePrefs.edit()
        editor.remove("gemini_api_key")
        editor.apply()
    }
}
