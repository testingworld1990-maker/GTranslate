package com.tanglishvideotranslator.audio

class AudioExtractor {
    fun extractChunksForDuration(durationMs: Long): List<LongRange> {
        return ChunkingEngine().chunkAudio(durationMs)
    }
}
