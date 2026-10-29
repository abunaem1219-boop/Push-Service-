package com.taqwanet.app.feature.quran.components

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.taqwanet.app.R

class PageFlipSoundHelper(private val context: Context) {
    private var soundPool: SoundPool? = null
    private var soundId: Int = 0
    private var isLoaded = false

    init {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build()

            val resId = context.resources.getIdentifier("page_flip", "raw", context.packageName)
            if (resId != 0) {
                soundId = soundPool?.load(context, resId, 1) ?: 0
                soundPool?.setOnLoadCompleteListener { _, _, status ->
                    if (status == 0) isLoaded = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playFlipSound(enabled: Boolean = true) {
        if (!enabled || !isLoaded) return
        try {
            soundPool?.play(soundId, 0.7f, 0.7f, 1, 0, 1.0f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}