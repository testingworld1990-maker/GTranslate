package com.tanglishvideotranslator.subtitles

import kotlin.math.abs

private const val DEFAULT_FONT_SIZE = 24f
private const val DEFAULT_BG_OPACITY = 0.4f

data class SubtitleStyle(
    val fontSize: Float = DEFAULT_FONT_SIZE,
    val textColor: Long = 0xFFFFFFFF,
    val backgroundColor: Long = 0xFF000000,
    val backgroundOpacity: Float = DEFAULT_BG_OPACITY,
    val position: SubtitlePosition = SubtitlePosition.Bottom,
    val dualDisplay: Boolean = false,
    val syncOffsetMs: Int = 0,
)

enum class SubtitlePosition {
    Top,
    Middle,
    Bottom,
}

data class SubtitleCue(
    val startMs: Long,
    val endMs: Long,
    val sourceLanguage: String,
    val originalText: String,
    val tanglishText: String,
)

class SubtitleOverlayController {
    fun currentCueAt(playheadMs: Long, cues: List<SubtitleCue>, syncOffsetMs: Int = 0): SubtitleCue? {
        val adjustedPlayhead = playheadMs + syncOffsetMs
        return cues.firstOrNull { cue ->
            adjustedPlayhead in cue.startMs..cue.endMs
        }
    }

    fun visibleCues(playheadMs: Long, cues: List<SubtitleCue>, lookAheadMs: Long = 2_000L): List<SubtitleCue> {
        val start = playheadMs
        val end = playheadMs + lookAheadMs
        return cues.filter { cue ->
            val overlap = (cue.startMs <= end && cue.endMs >= start)
            overlap
        }
    }

    fun computeTextStyle(
        style: SubtitleStyle,
        isPrimary: Boolean = true,
    ): SubtitleStyle {
        return style.copy(
            fontSize = if (isPrimary) style.fontSize else style.fontSize * 0.9f,
            backgroundOpacity = style.backgroundOpacity.coerceIn(0f, 1f),
        )
    }

    fun padText(text: String, targetLen: Int = 80): String {
        val trimmed = text.trim()
        return trimmed.padEnd(targetLen.coerceAtLeast(trimmed.length), ' ')
    }

    fun formatSubtitleText(cue: SubtitleCue, dualDisplay: Boolean): String {
        return if (dualDisplay) {
            "${cue.originalText}\n${cue.tanglishText}"
        } else {
            cue.tanglishText.ifBlank { cue.originalText }
        }
    }

    fun deduplicate(cues: List<SubtitleCue>): List<SubtitleCue> {
        val unique = linkedMapOf<String, SubtitleCue>()
        cues.forEach { cue ->
            val key = "${cue.startMs}:${cue.endMs}:${cue.originalText}:${cue.tanglishText}"
            unique[key] = cue
        }
        return unique.values.toList()
    }

    fun shouldDisplayAt(playheadMs: Long, cue: SubtitleCue, syncOffsetMs: Int): Boolean {
        val adjusted = playheadMs + syncOffsetMs
        return adjusted in cue.startMs..cue.endMs
    }

    fun normalizeOffset(offsetMs: Int): Int = offsetMs.coerceIn(-2000, 2000)
}
