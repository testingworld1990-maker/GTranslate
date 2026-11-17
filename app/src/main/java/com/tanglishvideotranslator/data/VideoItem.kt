package com.tanglishvideotranslator.data

import android.net.Uri

data class VideoItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val mimeType: String,
    val durationLabel: String,
    val isRecent: Boolean = false,
    val isFavorite: Boolean = false,
)
