// common/src/commonMain/kotlin/com/emgprosthetics/ui/domain/usecases/ConnectDeviceUseCase.kt
package com.emgprosthetics.ui.domain.usecases

import com.emgprosthetics.ui.data.models.*
import com.emgprosthetics.ui.data.repositories.EmgDataRepository
import kotlinx.coroutines.delay

class ConnectDeviceUseCase(
    private val emgDataRepository: EmgDataRepository
) {
    suspend fun execute(deviceId: String): Result<EmgDevice> {
        return try {
            // Simulate connection delay
            delay(1500)

            val device = EmgDevice(
                id = deviceId,
                name = "EMG Device #1",
                status = DeviceStatus.CONNECTED,
                batteryLevel = 85,
                signalQuality = 0.92f,
                channels = listOf(
                    EmgChannel(1, "Flexor", true, 0.95f),
                    EmgChannel(2, "Extensor", true, 0.88f),
                    EmgChannel(3, "Bicep", false, 0.0f),
                    EmgChannel(4, "Tricep", false, 0.0f)
                )
            )

            Result.success(device)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// common/src/commonMain/kotlin/com/emgprosthetics/ui/domain/usecases/CalibrateSystemUseCase.kt
class CalibrateSystemUseCase(
    private val emgDataRepository: EmgDataRepository
) {
    suspend fun execute(config: CalibrationConfig): Result<CalibrationState> {
        return try {
            // Simulate calibration process
            val totalSteps = config.gestureSequence.size
            var currentStep = 0

            config.gestureSequence.forEach { gesture ->
                currentStep++

                // Simulate each gesture calibration
                repeat(config.duration) { second ->
                    delay(1000) // 1 second per step

                    // Could emit progress updates here in real implementation
                }

                // Rest between gestures
                if (currentStep < totalSteps) {
                    delay((config.restBetweenGestures * 1000).toLong())
                }
            }

            val completedState = CalibrationState(
                isActive = false,
                isComplete = true,
                progress = 1.0f,
                collectedSamples = config.gestureSequence.size * config.duration * 2000 // sample rate
            )

            Result.success(completedState)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// common/src/commonMain/kotlin/com/emgprosthetics/ui/domain/usecases/MonitorSignalUseCase.kt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.sin
import kotlin.math.PI
import kotlin.random.Random

class MonitorSignalUseCase(
    private val emgDataRepository: EmgDataRepository
) {
    fun execute(deviceId: String): Flow<EmgSignalBatch> = flow {
        var sequenceNumber = 0L
        val sampleRate = 2000 // Hz
        val batchSize = 100 // samples per batch
        val channels = listOf(1, 2) // Active channels

        while (true) {
            val timestamp = System.currentTimeMillis()
            val samples = mutableListOf<EmgSample>()

            channels.forEach { channelId ->
                repeat(batchSize) { i ->
                    val timeIndex = sequenceNumber * batchSize + i

                    // Simulate EMG signal with noise
                    val baseFreq = when (channelId) {
                        1 -> 60.0 // Flexor frequency
                        2 -> 80.0 // Extensor frequency
                        else -> 50.0
                    }

                    val signal = generateEmgSignal(timeIndex, sampleRate, baseFreq)
                    val quality = 0.85f + Random.nextFloat() * 0.15f // 85-100% quality

                    samples.add(
                        EmgSample(
                            timestamp = timestamp + (i * 1000 / sampleRate),
                            channelId = channelId,
                            value = signal,
                            quality = quality
                        )
                    )
                }
            }

            val batch = EmgSignalBatch(
                deviceId = deviceId,
                samples = samples,
                sequenceNumber = sequenceNumber++,
                timestamp = timestamp
            )

            emit(batch)

            // Emit batches at ~50Hz (every 20ms)
            delay(20)
        }
    }

    private fun generateEmgSignal(
        timeIndex: Long,
        sampleRate: Int,
        baseFrequency: Double
    ): Float {
        val time = timeIndex.toDouble() / sampleRate

        // Base EMG signal components
        val signal1 = sin(2 * PI * baseFrequency * time)
        val signal2 = sin(2 * PI * (baseFrequency * 1.5) * time) * 0.3
        val signal3 = sin(2 * PI * (baseFrequency * 2.2) * time) * 0.15

        // Add noise
        val noise = (Random.nextFloat() - 0.5f) * 0.1f

        // Add random bursts (simulate muscle activation)
        val burstProbability = 0.02 // 2% chance per sample
        val burstAmplitude = if (Random.nextFloat() < burstProbability) {
            (Random.nextFloat() * 2.0f + 1.0f) // 1-3x amplitude
        } else {
            1.0f
        }

        val finalSignal = (signal1 + signal2 + signal3) * burstAmplitude + noise

        // Convert to microvolts and clamp
        return (finalSignal * 500f).coerceIn(-1000f, 1000f)
    }
}

// Base repository interfaces that will be implemented later
// common/src/commonMain/kotlin/com/emgprosthetics/ui/data/repositories/EmgDataRepository.kt
abstract class EmgDataRepository {
    abstract suspend fun connectDevice(deviceId: String): Result<EmgDevice>
    abstract suspend fun disconnectDevice(deviceId: String): Result<Unit>
    abstract suspend fun startSignalStream(deviceId: String): Flow<EmgSignalBatch>
    abstract suspend fun calibrateDevice(config: CalibrationConfig): Result<CalibrationState>
    abstract suspend fun getSystemMetrics(): Flow<SystemMetrics>
}

// Mock implementation for now
class MockEmgDataRepository : EmgDataRepository() {
    override suspend fun connectDevice(deviceId: String): Result<EmgDevice> {
        delay(1000)
        return Result.success(
            EmgDevice(
                id = deviceId,
                name = "Mock EMG Device",
                status = DeviceStatus.CONNECTED,
                batteryLevel = 75,
                signalQuality = 0.9f
            )
        )
    }

    override suspend fun disconnectDevice(deviceId: String): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }

    override suspend fun startSignalStream(deviceId: String): Flow<EmgSignalBatch> {
        return flow {
            var seq = 0L
            while (true) {
                emit(
                    EmgSignalBatch(
                        deviceId = deviceId,
                        samples = generateMockSamples(),
                        sequenceNumber = seq++
                    )
                )
                delay(20) // 50Hz
            }
        }
    }

    override suspend fun calibrateDevice(config: CalibrationConfig): Result<CalibrationState> {
        delay((config.duration * 1000).toLong())
        return Result.success(
            CalibrationState(
                isActive = false,
                isComplete = true,
                progress = 1f,
                collectedSamples = config.duration * 2000
            )
        )
    }

    override suspend fun getSystemMetrics(): Flow<SystemMetrics> {
        return flow {
            while (true) {
                emit(
                    SystemMetrics(
                        cpuUsage = Random.nextFloat() * 100,
                        memoryUsage = 300f + Random.nextFloat() * 200,
                        signalLatency = Random.nextLong(5, 25),
                        predictionLatency = Random.nextLong(10, 50),
                        frameRate = 58f + Random.nextFloat() * 4,
                        networkStatus = NetworkStatus.CONNECTED
                    )
                )
                delay(1000)
            }
        }
    }

    private fun generateMockSamples(): List<EmgSample> {
        val samples = mutableListOf<EmgSample>()
        val timestamp = System.currentTimeMillis()

        repeat(50) { i ->
            samples.add(
                EmgSample(
                    timestamp = timestamp + i,
                    channelId = 1,
                    value = (Random.nextFloat() - 0.5f) * 1000f,
                    quality = 0.8f + Random.nextFloat() * 0.2f
                )
            )

            samples.add(
                EmgSample(
                    timestamp = timestamp + i,
                    channelId = 2,
                    value = (Random.nextFloat() - 0.5f) * 800f,
                    quality = 0.85f + Random.nextFloat() * 0.15f
                )
            )
        }

        return samples
    }
}