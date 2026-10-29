package com.taqwanet.app.feature.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taqwanet.app.core.audio.QuranAudioPlayer
import com.taqwanet.app.data.local.AppDatabase
import com.taqwanet.app.data.local.entity.QuranBookmarkEntity
import com.taqwanet.app.data.repository.QuranRepository
import com.taqwanet.app.domain.model.Ayah
import com.taqwanet.app.domain.model.Reciter
import com.taqwanet.app.domain.model.Surah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuranUiState(
    val searchQuery: String = "",
    val surahs: List<Surah> = emptyList(),
    val bookmarks: List<QuranBookmarkEntity> = emptyList(),
    val selectedSurah: Surah? = null,
    val ayahsOfSelectedSurah: List<Ayah> = emptyList(),
    val selectedReciter: Reciter = Reciter.DEFAULT_RECITERS[0],
    val playingAyahNumber: Int? = null,
    val isPlayingAudio: Boolean = false
)

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = QuranRepository(db.quranBookmarkDao())
    private val audioPlayer = QuranAudioPlayer.getInstance(application)

    private val _uiState = MutableStateFlow(QuranUiState(surahs = repo.allSurahs))
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.bookmarks.collect { list ->
                _uiState.update { it.copy(bookmarks = list) }
            }
        }
        viewModelScope.launch {
            audioPlayer.isPlaying.collect { playing ->
                _uiState.update { it.copy(isPlayingAudio = playing) }
            }
        }
        viewModelScope.launch {
            audioPlayer.currentPlayingAyah.collect { ayahNum ->
                _uiState.update { it.copy(playingAyahNumber = ayahNum) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val filtered = if (query.isBlank()) {
            repo.allSurahs
        } else {
            repo.allSurahs.filter {
                it.nameBn.contains(query, ignoreCase = true) ||
                it.meaningBn.contains(query, ignoreCase = true) ||
                it.number.toString() == query.trim()
            }
        }
        _uiState.update { it.copy(searchQuery = query, surahs = filtered) }
    }

    fun selectSurah(surah: Surah) {
        val ayahs = repo.getAyahsForSurah(surah.number)
        _uiState.update { it.copy(selectedSurah = surah, ayahsOfSelectedSurah = ayahs) }
    }

    fun clearSelectedSurah() {
        audioPlayer.stopAudio()
        _uiState.update { it.copy(selectedSurah = null, ayahsOfSelectedSurah = emptyList()) }
    }

    fun playAyah(surahNumber: Int, ayahNumber: Int) {
        if (_uiState.value.playingAyahNumber == ayahNumber && _uiState.value.isPlayingAudio) {
            audioPlayer.stopAudio()
        } else {
            audioPlayer.playAyahAudio(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                reciterSubpath = _uiState.value.selectedReciter.cdnSubpath
            )
        }
    }

    fun toggleBookmark(surah: Surah, ayahNumber: Int) {
        viewModelScope.launch {
            repo.toggleBookmark(surah, ayahNumber)
        }
    }
}