// common/src/commonMain/kotlin/com/emgprosthetics/ui/data/repositories/ConfigRepository.kt
package com.emgprosthetics.ui.data.repositories

import kotlinx.coroutines.delay

abstract class ConfigRepository {
    abstract suspend fun loadConfig(filename: String): Result<String>
    abstract suspend fun saveConfig(filename: String, content: String): Result<Unit>
    abstract suspend fun getDefaultConfig(): Map<String, Any>
}

class MockConfigRepository : ConfigRepository() {
    private val configs = mutableMapOf<String, String>()

    override suspend fun loadConfig(filename: String): Result<String> {
        delay(100) // Simulate file I/O
        return configs[filename]?.let { Result.success(it) }
            ?: Result.failure(Exception("Config not found: $filename"))
    }

    override suspend fun saveConfig(filename: String, content: String): Result<Unit> {
        delay(100) // Simulate file I/O
        configs[filename] = content
        return Result.success(Unit)
    }

    override suspend fun getDefaultConfig(): Map<String, Any> {
        return mapOf(
            "device_control" to mapOf(
                "polling_interval_ms" to 1000,
                "connection_timeout_ms" to 5000,
                "max_retry_attempts" to 3,
                "auto_reconnect" to true
            ),
            "visualization" to mapOf(
                "max_points_per_channel" to 2000,
                "update_frequency_ms" to 16,
                "chart_colors" to listOf("#00D4FF", "#FF6B00", "#00FF88", "#FF3366"),
                "background_color" to "#1E1E2E",
                "grid_color" to "#404040"
            ),
            "performance" to mapOf(
                "use_hardware_acceleration" to true,
                "max_render_fps" to 60,
                "memory_limit_mb" to 512
            )
        )
    }
}

// common/src/commonMain/kotlin/com/emgprosthetics/ui/data/network/ApiClient.kt
package com.emgprosthetics.ui.data.network

import io.ktor.client.*
import kotlinx.coroutines.delay

class ApiClient(private val httpClient: HttpClient) {
    private val baseUrl = "http://localhost:8080"

    suspend fun startDevice(deviceId: String): Result<Map<String, Any>> {
        return try {
            delay(1000) // Simulate API call
            Result.success(mapOf(
                "status" to "success",
                "device_id" to deviceId,
                "message" to "Device started successfully"
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun stopDevice(deviceId: String): Result<Map<String, Any>> {
        return try {
            delay(500) // Simulate API call
            Result.success(mapOf(
                "status" to "success",
                "device_id" to deviceId,
                "message" to "Device stopped successfully"
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun calibrateDevice(deviceId: String, config: Map<String, Any>): Result<Map<String, Any>> {
        return try {
            delay(2000) // Simulate calibration API call
            Result.success(mapOf(
                "status" to "success",
                "device_id" to deviceId,
                "calibration_id" to "cal_${System.currentTimeMillis()}",
                "message" to "Calibration completed successfully"
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeviceStatus(deviceId: String): Result<Map<String, Any>> {
        return try {
            delay(200) // Simulate API call
            Result.success(mapOf(
                "device_id" to deviceId,
                "status" to "connected",
                "battery_level" to 85,
                "signal_quality" to 0.92,
                "last_seen" to System.currentTimeMillis()
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// common/src/commonMain/kotlin/com/emgprosthetics/ui/data/network/MessageBrokerClient.kt
package com.emgprosthetics.ui.data.network

import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlin.random.Random

class MessageBrokerClient(private val httpClient: HttpClient) {
    private var isConnected = false

    suspend fun connect(): Result<Unit> {
        return try {
            delay(1000) // Simulate connection
            isConnected = true
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun disconnect(): Result<Unit> {
        isConnected = false
        return Result.success(Unit)
    }

    fun subscribeToSignals(deviceId: String): Flow<Map<String, Any>> = flow {
        while (isConnected) {
            // Simulate real-time signal data
            val signalData = mapOf(
                "device_id" to deviceId,
                "timestamp" to System.currentTimeMillis(),
                "channels" to mapOf(
                    "1" to (Random.nextFloat() - 0.5f) * 1000,
                    "2" to (Random.nextFloat() - 0.5f) * 800
                ),
                "quality" to (0.8f + Random.nextFloat() * 0.2f)
            )

            emit(signalData)
            delay(20) // 50 Hz
        }
    }

    fun subscribeToGestures(deviceId: String): Flow<Map<String, Any>> = flow {
        val gestures = listOf("fist", "open_hand", "point", "rest")

        while (isConnected) {
            val gesture = gestures.random()
            val confidence = when (gesture) {
                "fist" -> 0.7f + Random.nextFloat() * 0.3f
                "open_hand" -> 0.6f + Random.nextFloat() * 0.4f
                "point" -> 0.5f + Random.nextFloat() * 0.5f
                else -> Random.nextFloat() * 0.3f
            }

            val gestureData = mapOf(
                "device_id" to deviceId,
                "timestamp" to System.currentTimeMillis(),
                "gesture" to gesture,
                "confidence" to confidence,
                "features" to mapOf(
                    "rms" to Random.nextFloat(),
                    "variance" to Random.nextFloat(),
                    "frequency_peak" to (50f + Random.nextFloat() * 100f)
                )
            )

            emit(gestureData)
            delay(100) // 10 Hz
        }
    }

    fun subscribeToSystemMetrics(): Flow<Map<String, Any>> = flow {
        while (isConnected) {
            val metricsData = mapOf(
                "timestamp" to System.currentTimeMillis(),
                "cpu_usage" to (30f + Random.nextFloat() * 40f),
                "memory_usage" to (400f + Random.nextFloat() * 200f),
                "signal_latency" to Random.nextLong(5, 30),
                "prediction_latency" to Random.nextLong(10, 50),
                "frame_rate" to (58f + Random.nextFloat() * 4f),
                "network_status" to "connected"
            )

            emit(metricsData)
            delay(1000) // 1 Hz
        }
    }

    fun isConnected(): Boolean = isConnected
}