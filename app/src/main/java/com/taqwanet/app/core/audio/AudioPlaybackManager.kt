package com.taqwanet.app.core.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.taqwanet.app.domain.model.PrayerType

class AudioPlaybackManager private constructor(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var lastPlayedTimestamp: Long = 0L

    private val audioFocusHelper = AudioFocusHelper(context) {
        stopPlayback()
    }

    // ৫টি ওয়াক্তের নির্ধারিত বাংলা ভয়েস ফাইল ম্যাপিং
    private val prayerVoiceFileMap = mapOf(
        PrayerType.FAJR to "fajr_complete_bn",
        PrayerType.DHUHR to "dhuhr_complete_bn",
        PrayerType.ASR to "asr_complete_bn",
        PrayerType.MAGHRIB to "maghrib_complete_bn",
        PrayerType.ISHA to "isha_complete_bn"
    )

    fun playPrayerCompletionVoice(prayerType: PrayerType, onCompletion: (() -> Unit)? = null) {
        // রিকম্পোজিশন বা দ্রুত ডাবল ট্যাপের কারণে একই ভয়েস পরপর দুইবার বাজা ঠেকানোর ফিল্টার
        val now = System.currentTimeMillis()
        if (now - lastPlayedTimestamp < 1500) return
        lastPlayedTimestamp = now

        val fileName = prayerVoiceFileMap[prayerType] ?: return
        playRawAudioResource(fileName, onCompletion)
    }

    fun playAdhan(isFull: Boolean, onCompletion: (() -> Unit)? = null) {
        val fileName = if (isFull) "adhan_full" else "adhan_short"
        playRawAudioResource(fileName, onCompletion)
    }

    fun previewNotificationTone(soundName: String, onCompletion: (() -> Unit)? = null) {
        playRawAudioResource(soundName, onCompletion)
    }

    @Synchronized
    private fun playRawAudioResource(resourceName: String, onCompletion: (() -> Unit)? = null) {
        try {
            stopPlayback()

            val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
            if (resId == 0) {
                Log.w("AudioPlaybackManager", "Audio resource '$resourceName' not found in res/raw. Skipping safely.")
                onCompletion?.invoke()
                return
            }

            if (!audioFocusHelper.requestAudioFocus()) {
                Log.w("AudioPlaybackManager", "Audio focus denied.")
                onCompletion?.invoke()
                return
            }

            val player = MediaPlayer.create(context, resId)
            if (player == null) {
                Log.e("AudioPlaybackManager", "Failed to create MediaPlayer for $resourceName")
                audioFocusHelper.abandonAudioFocus()
                onCompletion?.invoke()
                return
            }

            mediaPlayer = player
            player.setOnCompletionListener {
                stopPlayback()
                onCompletion?.invoke()
            }
            player.setOnErrorListener { _, what, extra ->
                Log.e("AudioPlaybackManager", "MediaPlayer error: what=$what, extra=$extra")
                stopPlayback()
                onCompletion?.invoke()
                true
            }
            player.start()
        } catch (e: Exception) {
            Log.e("AudioPlaybackManager", "Exception playing audio: ${e.message}", e)
            stopPlayback()
            onCompletion?.invoke()
        }
    }

    @Synchronized
    fun stopPlayback() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            Log.e("AudioPlaybackManager", "Error stopping playback: ${e.message}")
        } finally {
            mediaPlayer = null
            audioFocusHelper.abandonAudioFocus()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AudioPlaybackManager? = null

        fun getInstance(context: Context): AudioPlaybackManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AudioPlaybackManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}