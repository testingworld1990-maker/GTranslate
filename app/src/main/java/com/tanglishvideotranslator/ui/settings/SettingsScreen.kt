package com.tanglishvideotranslator.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var geminiApiKey by remember { mutableStateOf("") }
    var translationStyle by remember { mutableStateOf("Casual") }

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
                value = geminiApiKey,
                onValueChange = { geminiApiKey = it },
                label = { Text("Gemini API key") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Tanglish style: $translationStyle",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Button(
                onClick = { translationStyle = if (translationStyle == "Casual") "Formal" else "Casual" },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Toggle style")
            }
        }
    }
}
