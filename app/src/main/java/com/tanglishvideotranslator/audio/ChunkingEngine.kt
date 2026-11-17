package com.tanglishvideotranslator.audio

-import kotlin.math.max
-
-class ChunkingEngine {
-    fun chunkAudio(durationMs: Long, chunkSizeMs: Long = 25_000L, overlapMs: Long = 2_000L): List<LongRange> {
-        if (durationMs <= 0L) return emptyList()
-
-        val effectiveChunk = max(1L, chunkSizeMs)
-        val effectiveOverlap = minOf(max(0L, overlapMs), effectiveChunk - 1)
-        val step = effectiveChunk - effectiveOverlap
-
-        val ranges = mutableListOf<LongRange>()
-        var start = 0L
-        while (start < durationMs) {
-            val end = minOf(start + effectiveChunk, durationMs)
-            ranges += start until end
-            if (end >= durationMs) break
-            start += step
-        }
-        return ranges
-    }
-}
+import kotlin.math.max
+
+class ChunkingEngine {
+    fun chunkAudio(durationMs: Long, chunkSizeMs: Long = 25_000L, overlapMs: Long = 2_000L): List<LongRange> {
+        if (durationMs <= 0L) return emptyList()
+
+        val effectiveChunk = max(1L, chunkSizeMs)
+        val effectiveOverlap = minOf(max(0L, overlapMs), effectiveChunk - 1)
+        val step = effectiveChunk - effectiveOverlap
+
+        val ranges = mutableListOf<LongRange>()
+        var start = 0L
+        while (start < durationMs) {
+            val end = minOf(start + effectiveChunk, durationMs)
+            ranges += start until end
+            if (end >= durationMs) break
+            start += step
+        }
+        return ranges
+    }
+
+    fun chunkBySilence(
+        waveformSamples: List<Int>,
+        sampleRate: Int = 16_000,
+        silenceThreshold: Int = 300,
+        minChunkMs: Long = 18_000L,
+        maxChunkMs: Long = 30_000L,
+    ): List<LongRange> {
+        if (waveformSamples.isEmpty()) return emptyList()
+
+        val samplesPerSecond = sampleRate
+        val chunkSize = ((maxChunkMs * samplesPerSecond) / 1000L).toInt().coerceAtLeast(1)
+        val minChunkSize = ((minChunkMs * samplesPerSecond) / 1000L).toInt().coerceAtLeast(1)
+
+        val ranges = mutableListOf<LongRange>()
+        var start = 0
+        var lastSpeechIndex = 0
+
+        for (index in waveformSamples.indices) {
+            val amplitude = kotlin.math.abs(waveformSamples[index])
+            if (amplitude > silenceThreshold) {
+                lastSpeechIndex = index
+            }
+
+            if ((index - start) >= chunkSize) {
+                val end = if (lastSpeechIndex > start + minChunkSize) lastSpeechIndex else start + minChunkSize
+                ranges += start.toLong() until end.coerceAtMost(waveformSamples.size).toLong()
+                start = end.coerceAtMost(waveformSamples.size)
+            }
+        }
+
+        if (start < waveformSamples.size) {
+            ranges += start.toLong() until waveformSamples.size.toLong()
+        }
+
+        return ranges
+    }
+}
