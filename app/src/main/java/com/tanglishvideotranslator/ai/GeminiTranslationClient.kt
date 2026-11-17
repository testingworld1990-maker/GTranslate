package com.tanglishvideotranslator.ai

import org.json.JSONArray
import org.json.JSONObject

data class SubtitleCue(
    val startMs: Long,
    val endMs: Long,
    val sourceLanguage: String,
    val originalText: String,
    val tanglishText: String,
)

class GeminiTranslationClient(
    private val apiKey: String,
) {
    fun parseResponse(json: String): List<SubtitleCue> {
        val items = JSONArray(json)
        val cues = mutableListOf<SubtitleCue>()
        for (index in 0 until items.length()) {
            val item = items.getJSONObject(index)
            val cue = SubtitleCue(
                startMs = item.optLong("start_ms", 0L),
                endMs = item.optLong("end_ms", 0L),
                sourceLanguage = item.optString("source_language", "unknown"),
                originalText = item.optString("original_text", ""),
                tanglishText = item.optString("tanglish_text", ""),
            )
            cues += cue
        }
        return cues
    }

    fun buildPrompt(
        audioChunkText: String,
        style: String = "casual",
        glossary: Map<String, String> = emptyMap(),
    ): String {
        val glossaryText = if (glossary.isEmpty()) {
            "No user glossary provided."
        } else {
            glossary.entries.joinToString(separator = "\n") { (key, value) -> "$key -> $value" }
        }

        return """
            You are a speech-to-text and translation engine.
            Detect the source language automatically.
            Transcribe the spoken content and translate it into natural colloquial Tanglish using English letters.
            If the speech is already Tamil, write the Tamil as Romanized Tanglish.
            Respect the user glossary when it is provided.
            Return ONLY a JSON array of objects in the form:
            [{"start_ms": 0, "end_ms": 2500, "source_language": "en", "original_text": "hello world", "tanglish_text": "hello world"}]
            Style: $style
            Glossary:
            $glossaryText
            Audio content:
            $audioChunkText
        """.trimIndent()
    }

    fun validateJson(json: String): Boolean {
        return try {
            JSONArray(json)
            true
        } catch (_: Exception) {
            false
        }
    }
}
