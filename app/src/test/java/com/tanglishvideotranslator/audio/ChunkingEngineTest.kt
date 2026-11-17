package com.tanglishvideotranslator.audio

import org.junit.Assert.assertEquals
import org.junit.Test

class ChunkingEngineTest {
    @Test
    fun `chunking produces expected ranges for a 90 second clip`() {
        val engine = ChunkingEngine()
        val chunks = engine.chunkAudio(90_000L)

        assertEquals(4, chunks.size)
        assertEquals(0L..25_000L, chunks.first())
        assertEquals(23_000L..48_000L, chunks[1])
        assertEquals(46_000L..71_000L, chunks[2])
        assertEquals(69_000L..90_000L, chunks[3])
    }
}
