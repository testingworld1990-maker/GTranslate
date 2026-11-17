package com.tanglishvideotranslator.audio

class ChunkingEngine {
    fun chunkAudio(durationMs: Long): List<LongRange> {
        val chunkMs = 25000L
        val overlapMs = 2000L
        val totalChunks = ((durationMs - chunkMs) / (chunkMs - overlapMs)) + 1
        return (0 until totalChunks.coerceAtLeast(1)).map { index ->
            val start = index * (chunkMs - overlapMs)
            val end = (start + chunkMs).coerceAtMost(durationMs)
            start until end
        }
    }
}
