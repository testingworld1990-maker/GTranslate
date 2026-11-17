package com.tanglishvideotranslator.ai

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiRateLimitException(message: String) : RuntimeException(message)
class GeminiQuotaException(message: String) : RuntimeException(message)
class GeminiApiException(message: String) : RuntimeException(message)

data class SubtitleCue(
    val startMs: Long,
    val endMs: Long,
    val sourceLanguage: String,
    val originalText: String,
    val tanglishText: String,
)

class GeminiTranslationClient(
    private val apiKey: String,
    private val model: String = "gemini-2.0-flash",
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build(),
) {

    fun parseResponse(json: String): List<SubtitleCue> {
        val cleanedJson = sanitizeJson(json)
        val array = JSONArray(cleanedJson)
        val cues = mutableListOf<SubtitleCue>()
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            cues += SubtitleCue(
                startMs = item.optLong("start_ms", 0L),
                endMs = item.optLong("end_ms", 0L),
                sourceLanguage = item.optString("source_language", "unknown"),
                originalText = item.optString("original_text", ""),
                tanglishText = item.optString("tanglish_text", ""),
            )
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
            Detect the source language automatically.
            Transcribe the speech and translate it into compact, natural, colloquial Tanglish using English letters.
            If the source is already Tamil, romanize it naturally in Tanglish.
            Use the glossary below when relevant.
            Output ONLY valid JSON matching this structure:
            [{"start_ms": 0, "end_ms": 2500, "source_language": "en", "original_text": "hello world", "tanglish_text": "hello world"}]
            Style: $style
            Glossary:
            $glossaryText
            Speech content:
            $audioChunkText
        """.trimIndent()
    }

    fun validateJson(json: String): Boolean = try {
        JSONArray(sanitizeJson(json))
        true
    } catch (_: Exception) {
        false
    }

    fun requestTranslation(
        prompt: String,
        retries: Int = 3,
    ): List<SubtitleCue> {
        if (apiKey.isBlank()) {
            throw GeminiApiException("Gemini API key is missing. Add your key in Settings.")
        }

        var attempt = 0
        while (attempt <= retries) {
            try {
                val response = callGemini(prompt)
                val text = extractText(response)
                val parsed = parseResponse(text)
                if (parsed.isNotEmpty()) return parsed
                throw GeminiApiException("Gemini returned empty subtitle data.")
            } catch (error: GeminiRateLimitException) {
                if (attempt == retries) throw error
                sleepBackoff(attempt)
                attempt += 1
            } catch (error: GeminiQuotaException) {
                throw error
            } catch (error: IOException) {
                if (attempt == retries) throw GeminiApiException("Network error while contacting Gemini: ${error.message}")
                sleepBackoff(attempt)
                attempt += 1
            } catch (error: Exception) {
                if (attempt == retries) throw GeminiApiException("Failed to translate audio chunk: ${error.message}")
                sleepBackoff(attempt)
                attempt += 1
            }
        }
        throw GeminiApiException("Translation request failed after retries.")
    }

    private fun callGemini(prompt: String): String {
        val requestBody = JSONObject().apply {
            put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", prompt)))))
            put("generationConfig", JSONObject().put("response_mime_type", "application/json"))
        }.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val message = body.ifBlank { "Gemini request failed" }
                when {
                    "RESOURCE_EXHAUSTED" in message || "429" in message -> throw GeminiRateLimitException("Gemini rate limit exceeded. Please wait and retry.")
                    "quota" in message.lowercase() || "QUOTA" in message -> throw GeminiQuotaException("Gemini API quota exhausted. Check your billing and quota settings.")
                    else -> throw GeminiApiException("Gemini request failed: $message")
                }
            }
            if (body.isBlank()) throw GeminiApiException("Gemini returned an empty response body.")
            return body
        }
    }

    private fun extractText(responseBody: String): String {
        val root = JSONObject(responseBody)
        val candidates = root.getJSONArray("candidates")
        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.getJSONObject("content")
        val parts = content.getJSONArray("parts")
        val builder = StringBuilder()
        for (index in 0 until parts.length()) {
            val part = parts.getJSONObject(index)
            if (part.has("text")) builder.append(part.getString("text"))
        }
        return builder.toString()
    }

    private fun sanitizeJson(raw: String): String {
        var clean = raw.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json").trim()
        }
        if (clean.startsWith("```")) {
            clean = clean.removePrefix("```").trim()
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```").trim()
        }
        return clean
    }

    private fun sleepBackoff(attempt: Int) {
        val millis = (1000L * (1L shl attempt.coerceAtMost(4))).coerceAtMost(5000L)
        Thread.sleep(millis)
    }
}
