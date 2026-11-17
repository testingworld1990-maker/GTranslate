package com.tanglishvideotranslator.cache

import android.util.Base64
import java.security.MessageDigest

class VideoHashUtils {
    fun hashVideoPath(path: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(path.toByteArray())
        return Base64.encodeToString(bytes, Base64.NO_WRAP or Base64.NO_PADDING).replace("/", "_")
    }

    fun hashBytes(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return Base64.encodeToString(digest.digest(bytes), Base64.NO_WRAP or Base64.NO_PADDING).replace("/", "_")
    }
}
