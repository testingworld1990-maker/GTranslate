package com.tanglishvideotranslator.ui.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun VideoPlayerView(player: ExoPlayer) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                this.player = player
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                useController = true
                controllerAutoShow = true
            }
        },
        update = {
            it.player = player
        }
    )
}
