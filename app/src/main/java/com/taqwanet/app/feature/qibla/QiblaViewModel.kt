package com.taqwanet.app.feature.qibla

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taqwanet.app.core.calculation.QiblaMath
import com.taqwanet.app.core.sensor.QiblaSensorManager
import com.taqwanet.app.data.local.datastore.AppPreferencesDataSource
import com.taqwanet.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class QiblaUiState(
    val qiblaBearing: Double = 268.0, // ঢাকা থেকে প্রায় ২৬৮ ডিগ্রি
    val distanceKm: Int = 5150,
    val currentCompassAzimuth: Float = 0f,
    val needleRotationAngle: Float = 0f,
    val isSensorAvailable: Boolean = true,
    val cityNameBn: String = "ঢাকা"
)

class QiblaViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorManager = QiblaSensorManager(application)
    private val prefs = AppPreferencesDataSource(application)
    private val settingsRepo = SettingsRepository(prefs)

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepo.selectedCityName,
                settingsRepo.coordinates,
                sensorManager.azimuthFlow,
                sensorManager.isSensorAvailable
            ) { city, coords, azimuth, hasSensor ->
                val bearing = QiblaMath.calculateQiblaBearing(coords.first, coords.second)
                val dist = QiblaMath.calculateDistanceToKaabaKm(coords.first, coords.second)
                // সুই সবসময় কাবার দিকে তাক করে থাকবে
                val needleAngle = (bearing.toFloat() - azimuth + 360f) % 360f

                QiblaUiState(
                    qiblaBearing = bearing,
                    distanceKm = dist,
                    currentCompassAzimuth = azimuth,
                    needleRotationAngle = needleAngle,
                    isSensorAvailable = hasSensor,
                    cityNameBn = city
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun startSensors() = sensorManager.startListening()
    fun stopSensors() = sensorManager.stopListening()
}