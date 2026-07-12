package com.kutluhangul.liftgenius.ui.components

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.kutluhangul.liftgenius.R

/**
 * Looping, muted neon-logo video for the welcome screen. Center-cropped (zoom) with no
 * player chrome. The ExoPlayer is released when the composable leaves composition.
 */
@Composable
fun NeonVideo(modifier: Modifier = Modifier) {
    val exoPlayer = rememberExoPlayer()

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
        },
    )

    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }
}

@Composable
private fun rememberExoPlayer(): ExoPlayer {
    val context = LocalContext.current
    return androidx.compose.runtime.remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = "android.resource://${context.packageName}/${R.raw.welcome_neon}".toUri()
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
            playWhenReady = true
            prepare()
        }
    }
}
