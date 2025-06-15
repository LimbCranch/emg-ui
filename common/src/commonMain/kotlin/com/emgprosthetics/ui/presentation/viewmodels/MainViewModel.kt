// common/src/commonMain/kotlin/com/emgprosthetics/ui/presentation/viewmodels/MainViewModel.kt
package com.emgprosthetics.ui.presentation.viewmodels

import com.emgprosthetics.ui.data.models.*
import com.emgprosthetics.ui.domain.usecases.ConnectDeviceUseCase
import com.emgprosthetics.ui.domain.usecases.CalibrateSystemUseCase
import com.emgprosthetics.ui.domain.usecases.MonitorSignalUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val connectDeviceUseCase: ConnectDeviceUseCase,
    private val calibrateSystemUseCase: CalibrateSystemUseCase,
    private val monitorSignalUseCase: MonitorSignalUseCase
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Device state
    private val _deviceState = MutableStateFlow(
        EmgDevice(
            id = "emg_device_01",
            name = "EMG Device #1",
            status = DeviceStatus.DISCONNECTED,
            batteryLevel = null,
            signalQuality = 0f,
            channels = listOf(
                EmgChannel(1, "Channel 1", true, 0f),
                EmgChannel(2, "Channel 2", true, 0f),
                EmgChannel(3, "Channel 3", false, 0f),
                EmgChannel(4, "Channel 4", false, 0f)
            )
        )
    )
    val deviceState: StateFlow<EmgDevice> = _deviceState.asStateFlow()

    // Signal state
    private val _signalState = MutableStateFlow(
        SignalState(
            isReceiving = false,
            channels = emptyMap(),
            sampleRate = 2000,
            bufferSize = 2000,
            latency = 0
        )
    )
    val signalState: StateFlow<SignalState> = _signalState.asStateFlow()

    // System state
    private val _systemState = MutableStateFlow(
        SystemMetrics(
            cpuUsage = 0f,
            memoryUsage = 0f,
            signalLatency = 0,
            predictionLatency = 0,
            frameRate = 60f,
            networkStatus = NetworkStatus.DISCONNECTED
        )
    )
    val systemState: StateFlow<SystemMetrics> = _systemState.asStateFlow()

    // Gesture predictions
    private val _gestureState = MutableStateFlow<GesturePrediction?>(null)
    val gestureState: StateFlow<GesturePrediction?> = _gestureState.asStateFlow()

    // Calibration state
    private val _calibrationState = MutableStateFlow(CalibrationState())
    val calibrationState: StateFlow<CalibrationState> = _calibrationState.asStateFlow()

    init {
        // Start monitoring system metrics
        startSystemMonitoring()

        // Simulate some initial state
        simulateDeviceConnection()
    }

    fun startDevice() {
        viewModelScope.launch {
            try {
                _deviceState.value = _deviceState.value.copy(status = DeviceStatus.CONNECTING)

                // Simulate connection process
                kotlinx.coroutines.delay(2000)

                val result = connectDeviceUseCase.execute(_deviceState.value.id)

                _deviceState.value = _deviceState.value.copy(
                    status = DeviceStatus.CONNECTED,
                    batteryLevel = 85,
                    signalQuality = 0.92f
                )

                // Start signal monitoring
                startSignalMonitoring()

            } catch (e: Exception) {
                _deviceState.value = _deviceState.value.copy(status = DeviceStatus.ERROR)
            }
        }
    }

    fun stopDevice() {
        viewModelScope.launch {
            _deviceState.value = _deviceState.value.copy(
                status = DeviceStatus.DISCONNECTED,
                batteryLevel = null,
                signalQuality = 0f
            )

            _signalState.value = _signalState.value.copy(
                isReceiving = false,
                channels = emptyMap()
            )
        }
    }

    fun startCalibration() {
        viewModelScope.launch {
            try {
                _deviceState.value = _deviceState.value.copy(status = DeviceStatus.CALIBRATING)
                _calibrationState.value = CalibrationState(isActive = true)

                val config = CalibrationConfig(
                    duration = 30,
                    gestureSequence = listOf(
                        GestureType.REST,
                        GestureType.FIST,
                        GestureType.OPEN_HAND,
                        GestureType.POINT
                    ),
                    restBetweenGestures = 5
                )

                // Simulate calibration process
                val result = calibrateSystemUseCase.execute(config)

                // Update calibration progress
                for (progress in 0..100 step 10) {
                    _calibrationState.value = _calibrationState.value.copy(
                        progress = progress / 100f,
                        remainingTime = 30 - (progress * 30 / 100)
                    )
                    kotlinx.coroutines.delay(300)
                }

                _calibrationState.value = CalibrationState(
                    isActive = false,
                    isComplete = true,
                    progress = 1f
                )

                _deviceState.value = _deviceState.value.copy(status = DeviceStatus.ACTIVE)

            } catch (e: Exception) {
                _calibrationState.value = CalibrationState(isActive = false)
                _deviceState.value = _deviceState.value.copy(status = DeviceStatus.ERROR)
            }
        }
    }

    private fun startSignalMonitoring() {
        viewModelScope.launch {
            _signalState.value = _signalState.value.copy(isReceiving = true)

            // Simulate signal flow
            monitorSignalUseCase.execute(_deviceState.value.id)
                .collect { signalBatch ->
                    // Process signal batch and update state
                    val channelData = signalBatch.samples.groupBy { it.channelId }

                    _signalState.value = _signalState.value.copy(
                        channels = channelData.mapValues { (_, samples) ->
                            ChannelData(
                                samples = samples.map { it.value },
                                quality = samples.firstOrNull()?.quality ?: 0f,
                                isActive = true
                            )
                        },
                        latency = System.currentTimeMillis() - signalBatch.timestamp
                    )

                    // Simulate gesture prediction
                    if (signalBatch.samples.isNotEmpty()) {
                        val prediction = simulateGesturePrediction(signalBatch)
                        _gestureState.value = prediction
                    }
                }
        }
    }

    private fun startSystemMonitoring() {
        viewModelScope.launch {
            while (true) {
                _systemState.value = SystemMetrics(
                    cpuUsage = (30..60).random().toFloat(),
                    memoryUsage = (400..600).random().toFloat(),
                    signalLatency = (10..30).random().toLong(),
                    predictionLatency = (20..50).random().toLong(),
                    frameRate = (58..60).random().toFloat(),
                    networkStatus = if (_deviceState.value.status == DeviceStatus.CONNECTED)
                        NetworkStatus.CONNECTED else NetworkStatus.DISCONNECTED
                )
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun simulateDeviceConnection() {
        // Simulate initial device discovery
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _deviceState.value = _deviceState.value.copy(
                status = DeviceStatus.DISCONNECTED,
                signalQuality = 0f
            )
        }
    }

    private fun simulateGesturePrediction(signalBatch: EmgSignalBatch): GesturePrediction {
        // Simple simulation of gesture prediction
        val gestures = listOf(
            GestureType.FIST to 0.85f,
            GestureType.OPEN_HAND to 0.12f,
            GestureType.POINT to 0.03f,
            GestureType.REST to 0.0f
        )

        val (gestureType, confidence) = gestures.random()

        return GesturePrediction(
            gestureType = gestureType,
            confidence = confidence,
            features = mapOf(
                "rms" to signalBatch.samples.map { it.value * it.value }.average().toFloat(),
                "variance" to (0.1f..0.9f).random(),
                "frequency_peak" to (50f..150f).random()
            )
        )
    }
}

// Additional data classes for UI state
data class SignalState(
    val isReceiving: Boolean,
    val channels: Map<Int, ChannelData>,
    val sampleRate: Int,
    val bufferSize: Int,
    val latency: Long
)

data class ChannelData(
    val samples: List<Float>,
    val quality: Float,
    val isActive: Boolean
)