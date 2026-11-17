package com.tanglishvideotranslator.export

import java.util.Locale

private fun formatTimestampMs(timestampMs: Long): String {
    val totalMs = timestampMs.coerceAtLeast(0L)
    val hours = (totalMs / 3_600_000L)
    val minutes = (totalMs % 3_600_000L) / 60_000L
    val seconds = (totalMs % 60_000L) / 1_000L
    val millis = totalMs % 1_000L
    return String.format(Locale.US, "%02d:%02d:%02d,%03d", hours, minutes, seconds, millis)
}

data class ExportCue(
    val startMs: Long,
    val endMs: Long,
    val text: String,
    val originalText: String? = null,
)

class SubtitleExporter {
    fun exportSrt(entries: List<String>): String = entries.joinToString(separator = "\n\n")
    fun exportSrt(entries: List<ExportCue>): String {
        val lines = mutableListOf<String>()
        entries.forEachIndexed { index, cue ->
            lines += "${index + 1}"
            lines += "${formatTimestampMs(cue.startMs)} --> ${formatTimestampMs(cue.endMs)}"
            lines += cue.originalText?.takeIf { it.isNotBlank() } ?: cue.text
            lines += ""
        }
        return lines.joinToString(separator = "\n")
    }

    fun exportVtt(entries: List<String>): String = entries.joinToString(separator = "\n\n")
    fun exportVtt(entries: List<ExportCue>): String {
        val lines = mutableListOf<String>()
        lines += "WEBVTT"
        lines += ""
        entries.forEachIndexed { index, cue ->
            lines += "${index + 1}"
            lines += "${formatTimestampMs(cue.startMs)} --> ${formatTimestampMs(cue.endMs)}"
            lines += cue.originalText?.takeIf { it.isNotBlank() } ?: cue.text
            lines += ""
        }
        return lines.joinToString(separator = "\n")
    }

    fun exportTxt(entries: List<String>): String = entries.joinToString(separator = "\n")
    fun exportTxt(entries: List<ExportCue>, mode: ExportMode = ExportMode.TanglishOnly): String {
        val lines = entries.map { cue ->
            when (mode) {
                ExportMode.TanglishOnly -> cue.text
                ExportMode.OriginalOnly -> cue.originalText ?: cue.text
                ExportMode.Both -> "${cue.originalText ?: cue.text} | ${cue.text}"
            }
        }
        return lines.joinToString(separator = "\n")
    }
}

enum class ExportMode {
    TanglishOnly,
    OriginalOnly,
    Both,
}
