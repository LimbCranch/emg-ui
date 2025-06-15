// common/src/commonMain/kotlin/com/emgprosthetics/ui/data/models/DeviceStatus.kt
package com.emgprosthetics.ui.data.models

import kotlinx.serialization.Serializable

@Serializable
enum class DeviceStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    CALIBRATING,
    ACTIVE,
    ERROR
}

@Serializable
data class EmgDevice(
    val id: String,
    val name: String,
    val status: DeviceStatus,
    val batteryLevel: Int? = null,
    val signalQuality: Float = 0f,
    val channels: List<EmgChannel> = emptyList(),
    val lastSeen: Long = System.currentTimeMillis()
)

@Serializable
data class EmgChannel(
    val id: Int,
    val name: String,
    val isActive: Boolean = true,
    val quality: Float = 0f,
    val gain: Float = 1.0f
)

// common/src/commonMain/kotlin/com/emgprosthetics/ui/data/models/EmgSample.kt
@Serializable
data class EmgSample(
    val timestamp: Long,
    val channelId: Int,
    val value: Float,
    val quality: Float = 1.0f
)

@Serializable
data class EmgSignalBatch(
    val deviceId: String,
    val samples: List<EmgSample>,
    val sequenceNumber: Long,
    val timestamp: Long = System.currentTimeMillis()
)

// common/src/commonMain/kotlin/com/emgprosthetics/ui/data/models/GestureCommand.kt
@Serializable
enum class GestureType {
    FIST,
    OPEN_HAND,
    POINT,
    PINCH,
    FLEXOR,
    EXTENSOR,
    BICEP,
    TRICEP,
    REST
}

@Serializable
data class GesturePrediction(
    val gestureType: GestureType,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val features: Map<String, Float> = emptyMap()
)

@Serializable
data class GestureCommand(
    val deviceId: String,
    val prediction: GesturePrediction,
    val isExecuted: Boolean = false,
    val executionTime: Long? = null
)

// Calibration models
@Serializable
data class CalibrationConfig(
    val duration: Int, // seconds
    val gestureSequence: List<GestureType>,
    val restBetweenGestures: Int, // seconds
    val minimumConfidence: Float = 0.8f
)

@Serializable
data class CalibrationState(
    val isActive: Boolean = false,
    val currentGesture: GestureType? = null,
    val progress: Float = 0f,
    val remainingTime: Int = 0,
    val collectedSamples: Int = 0,
    val isComplete: Boolean = false
)

// System monitoring models
@Serializable
data class SystemMetrics(
    val timestamp: Long = System.currentTimeMillis(),
    val cpuUsage: Float,
    val memoryUsage: Float,
    val signalLatency: Long, // milliseconds
    val predictionLatency: Long, // milliseconds
    val frameRate: Float,
    val networkStatus: NetworkStatus
)

@Serializable
enum class NetworkStatus {
    CONNECTED,
    DISCONNECTED,
    POOR_CONNECTION,
    HIGH_LATENCY
}

@Serializable
data class SystemLog(
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel,
    val component: String,
    val message: String,
    val details: Map<String, String> = emptyMap()
)

@Serializable
enum class LogLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR
}