package com.tanglishvideotranslator.cache

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class VideoHashUtilsTest {
    @Test
    fun `hash is stable and non-empty`() {
        val utils = VideoHashUtils()
        val hashA = utils.hashVideoPath("/storage/emulated/0/video.mp4")
        val hashB = utils.hashVideoPath("/storage/emulated/0/video.mp4")

        assertEquals(hashA, hashB)
        assertNotNull(hashA)
        assert(hashA.isNotEmpty())
    }
}
