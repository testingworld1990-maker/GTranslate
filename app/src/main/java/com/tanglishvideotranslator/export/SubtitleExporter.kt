package com.tanglishvideotranslator.export

class SubtitleExporter {
    fun exportSrt(entries: List<String>): String = entries.joinToString(separator = "\n\n")
    fun exportVtt(entries: List<String>): String = entries.joinToString(separator = "\n\n")
    fun exportTxt(entries: List<String>): String = entries.joinToString(separator = "\n")
}
