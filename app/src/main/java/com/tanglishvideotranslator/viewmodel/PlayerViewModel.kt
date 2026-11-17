package com.tanglishvideotranslator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val player: ExoPlayer
) : ViewModel() {
    fun setPlaybackSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
    }

    fun seekBy(ms: Long) {
        player.seekBack()
    }
}
