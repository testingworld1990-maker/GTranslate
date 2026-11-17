package com.tanglishvideotranslator.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiTranslationClientTest {
    @Test
    fun `json response parses into subtitle cues`() {
        val json = """
            [{
              "start_ms": 1000,
              "end_ms": 2500,
              "source_language": "en",
              "original_text": "hello world",
              "tanglish_text": "hello world"
            }]
        """.trimIndent()

        val client = GeminiTranslationClient("test-key")
        val result = client.parseResponse(json)

        assertEquals(1, result.size)
        assertEquals(1000L, result[0].startMs)
        assertEquals("en", result[0].sourceLanguage)
        assertEquals("hello world", result[0].originalText)
    }

    @Test
    fun `prompt builder includes glossary and style instructions`() {
        val client = GeminiTranslationClient("test-key")
        val prompt = client.buildPrompt("sample audio", "formal", mapOf("hello" to "hlo"))

        assertTrue(prompt.contains("formal"))
        assertTrue(prompt.contains("hello -> hlo"))
        assertTrue(prompt.contains("Output ONLY valid JSON"))
    }
}
