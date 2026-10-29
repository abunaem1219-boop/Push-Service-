package com.taqwanet.app.core.audio

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuranAudioPlayer private constructor(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayingAyah = MutableStateFlow<Int?>(null)
    val currentPlayingAyah: StateFlow<Int?> = _currentPlayingAyah.asStateFlow()

    fun playAyahAudio(
        surahNumber: Int,
        ayahNumber: Int,
        reciterSubpath: String = "Alafasy_128kbps",
        onCompletion: (() -> Unit)? = null
    ) {
        stopAudio()

        // EveryAyah CDN URL ফরম্যাট: 001001.mp3 (সূরা ৩ ডিজিট + আয়াত ৩ ডিজিট)
        val formattedSurah = String.format("%03d", surahNumber)
        val formattedAyah = String.format("%03d", ayahNumber)
        val url = "https://everyayah.com/data/$reciterSubpath/$formattedSurah$formattedAyah.mp3"

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener {
                    start()
                    _isPlaying.value = true
                    _currentPlayingAyah.value = ayahNumber
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPlayingAyah.value = null
                    onCompletion?.invoke()
                }
                setOnErrorListener { _, _, _ ->
                    stopAudio()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            stopAudio()
        }
    }

    fun stopAudio() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
            _currentPlayingAyah.value = null
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: QuranAudioPlayer? = null

        fun getInstance(context: Context): QuranAudioPlayer {
            return INSTANCE ?: synchronized(this) {
                val instance = QuranAudioPlayer(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}