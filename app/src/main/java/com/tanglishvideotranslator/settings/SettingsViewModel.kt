package com.tanglishvideotranslator.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsStore.settingsFlow.collectLatest { appSettings ->
                _uiState.value = SettingsUiState(settings = appSettings, isLoading = false)
            }
        }
    }

    fun updateSettings(update: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            settingsStore.updateSettings(update)
        }
    }

    fun setApiKey(value: String) {
        viewModelScope.launch {
            settingsStore.setGeminiApiKey(value)
        }
    }

    fun setConsentGranted(value: Boolean) {
        viewModelScope.launch {
            settingsStore.setConsentGranted(value)
        }
    }
}
