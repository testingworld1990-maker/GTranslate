package com.tanglishvideotranslator.audio

import kotlin.math.max

class ChunkingEngine {
    fun chunkAudio(durationMs: Long, chunkSizeMs: Long = 25_000L, overlapMs: Long = 2_000L): List<LongRange> {
        if (durationMs <= 0L) return emptyList()

        val effectiveChunk = max(1L, chunkSizeMs)
        val effectiveOverlap = minOf(max(0L, overlapMs), effectiveChunk - 1)
        val step = effectiveChunk - effectiveOverlap

        val ranges = mutableListOf<LongRange>()
        var start = 0L
        while (start < durationMs) {
            val end = minOf(start + effectiveChunk, durationMs)
            ranges += start until end
            if (end >= durationMs) break
            start += step
        }
        return ranges
    }
}
