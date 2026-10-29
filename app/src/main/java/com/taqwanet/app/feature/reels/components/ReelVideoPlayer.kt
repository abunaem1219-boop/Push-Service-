package com.taqwanet.app.feature.reels.components

import android.view.LayoutInflater
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.taqwanet.app.R
import com.taqwanet.app.core.theme.MintBright

@OptIn(UnstableApi::class)
@Composable
fun ReelVideoPlayer(
    videoUrl: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isBuffering by remember { mutableStateOf(true) }
    var isUserPaused by remember { mutableStateOf(false) }

    // অপ্টিমাইজড এক্সোপ্লেয়ার
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            if (videoUrl.isNotBlank()) {
                val mediaItem = MediaItem.fromUri(videoUrl)
                setMediaItem(mediaItem)
                repeatMode = Player.REPEAT_MODE_ONE
                prepare()
            }
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    isBuffering = (playbackState == Player.STATE_BUFFERING)
                }
            })
        }
    }

    LaunchedEffect(isPlaying, isUserPaused) {
        if (isPlaying && !isUserPaused) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    DisposableEffect(videoUrl) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // স্ক্রিনে ট্যাপ করলে প্লে/পজ হবে
                isUserPaused = !isUserPaused
            },
        contentAlignment = Alignment.Center
    ) {
        // টেক্সচারভিউ সমৃদ্ধ PlayerView ইনফ্লেশন (কালো স্ক্রিন ফিক্স)
        AndroidView(
            factory = { ctx ->
                val view = LayoutInflater.from(ctx).inflate(R.layout.item_reel_player, null, false) as PlayerView
                view.player = exoPlayer
                view
            },
            modifier = Modifier.fillMaxSize()
        )

        // বাফারিং লোডিং স্পিনার
        if (isBuffering && isPlaying) {
            CircularProgressIndicator(
                color = MintBright,
                modifier = Modifier.size(44.dp)
            )
        }

        // ইউজার পজ করলে মাঝখানে প্লে আইকন প্রদর্শিত হবে
        AnimatedVisibility(
            visible = isUserPaused,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
        }
    }
}