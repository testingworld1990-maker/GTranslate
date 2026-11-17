package com.tanglishvideotranslator.export

import org.junit.Assert.assertTrue
import org.junit.Test

class SubtitleExporterTest {
    @Test
    fun `srt and vtt formatting produce valid content`() {
        val exporter = SubtitleExporter()
        val entries = listOf(
            "00:00:01,000 --> 00:00:03,000\nhello",
            "00:00:04,000 --> 00:00:06,000\nworld"
        )

        val srt = exporter.exportSrt(entries)
        val vtt = exporter.exportVtt(entries)

        assertTrue(srt.contains("00:00:01,000 --> 00:00:03,000"))
        assertTrue(vtt.contains("00:00:01,000 --> 00:00:03,000"))
    }
}
