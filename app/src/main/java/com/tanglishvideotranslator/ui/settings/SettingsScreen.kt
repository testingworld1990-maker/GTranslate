package com.tanglishvideotranslator.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tanglishvideotranslator.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings = uiState.settings

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Audio is sent to Google Gemini for speech recognition and translation. You must consent before use.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = settings.geminiApiKey,
                onValueChange = { viewModel.setApiKey(it) },
                label = { Text("Gemini API key") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "I consent to sending audio to Google Gemini",
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = settings.userConsentGiven,
                    onCheckedChange = { viewModel.setConsentGranted(it) }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text("Tanglish style: ${settings.tanglishStyle}", modifier = Modifier.weight(1f))
                Switch(
                    checked = settings.tanglishStyle == "formal",
                    onCheckedChange = {
                        viewModel.updateSettings { it.copy(tanglishStyle = if (it.tanglishStyle == "casual") "formal" else "casual") }
                    }
                )
            }

            Text(
                text = "Subtitle position: ${settings.subtitlePosition}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Button(
                onClick = {
                    viewModel.updateSettings {
                        it.copy(
                            subtitlePosition = when (it.subtitlePosition) {
                                "top" -> "middle"
                                "middle" -> "bottom"
                                else -> "top"
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cycle subtitle position")
            }
        }
    }
}
