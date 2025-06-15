// desktop/src/main/kotlin/com/emgprosthetics/desktop/platform/BluetoothManager.kt
package com.emgprosthetics.desktop.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class BluetoothManager {
    private var isEnabled = false
    private val discoveredDevices = mutableListOf<BluetoothDevice>()

    suspend fun enableBluetooth(): Result<Unit> {
        return try {
            delay(1000) // Simulate enable delay
            isEnabled = true
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun disableBluetooth(): Result<Unit> {
        isEnabled = false
        discoveredDevices.clear()
        return Result.success(Unit)
    }

    fun scanForDevices(): Flow<BluetoothDevice> = flow {
        if (!isEnabled) return@flow

        // Simulate device discovery
        val mockDevices = listOf(
            BluetoothDevice("EMG_001", "EMG Device #1", "00:11:22:33:44:55", -45),
            BluetoothDevice("EMG_002", "EMG Device #2", "00:11:22:33:44:66", -65),
            BluetoothDevice("EMG_PRO", "EMG Pro Device", "00:11:22:33:44:77", -35)
        )

        mockDevices.forEach { device ->
            delay((1000..3000).random().toLong())
            discoveredDevices.add(device)
            emit(device)
        }
    }

    suspend fun connectToDevice(deviceId: String): Result<Unit> {
        delay(2000) // Simulate connection time
        return if (discoveredDevices.any { it.id == deviceId }) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Device not found"))
        }
    }

    fun isBluetoothEnabled(): Boolean = isEnabled
    fun getDiscoveredDevices(): List<BluetoothDevice> = discoveredDevices.toList()
}

data class BluetoothDevice(
    val id: String,
    val name: String,
    val address: String,
    val rssi: Int // Signal strength
)

// desktop/src/main/kotlin/com/emgprosthetics/desktop/platform/FileSystem.kt
package com.emgprosthetics.desktop.platform

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.createDirectories

class FileSystem {
    private val appDataDir: Path by lazy {
        val userHome = System.getProperty("user.home")
        val osName = System.getProperty("os.name").lowercase()

        when {
            osName.contains("windows") -> Paths.get(userHome, "AppData", "Local", "EMGProsthetics")
            osName.contains("mac") -> Paths.get(userHome, "Library", "Application Support", "EMGProsthetics")
            else -> Paths.get(userHome, ".emg-prosthetics")
        }
    }

    init {
        // Create app data directory if it doesn't exist
        if (!appDataDir.exists()) {
            appDataDir.createDirectories()
        }
    }

    fun getConfigPath(): Path = appDataDir.resolve("config")
    fun getLogsPath(): Path = appDataDir.resolve("logs")
    fun getDataPath(): Path = appDataDir.resolve("data")
    fun getCachePath(): Path = appDataDir.resolve("cache")

    suspend fun saveConfiguration(filename: String, content: String): Result<Unit> {
        return try {
            val configDir = getConfigPath()
            if (!configDir.exists()) configDir.createDirectories()

            val file = configDir.resolve(filename)
            Files.writeString(file, content)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadConfiguration(filename: String): Result<String> {
        return try {
            val file = getConfigPath().resolve(filename)
            if (file.exists()) {
                val content = Files.readString(file)
                Result.success(content)
            } else {
                Result.failure(Exception("Configuration file not found: $filename"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveEmgData(sessionId: String, data: ByteArray): Result<Unit> {
        return try {
            val dataDir = getDataPath().resolve("sessions")
            if (!dataDir.exists()) dataDir.createDirectories()

            val file = dataDir.resolve("$sessionId.emg")
            Files.write(file, data)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportData(sessionId: String, exportPath: String): Result<Unit> {
        return try {
            val sourceFile = getDataPath().resolve("sessions").resolve("$sessionId.emg")
            val targetFile = Paths.get(exportPath)

            if (sourceFile.exists()) {
                Files.copy(sourceFile, targetFile)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Session data not found: $sessionId"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAppDataDirectory(): Path = appDataDir

    fun clearCache(): Result<Unit> {
        return try {
            val cacheDir = getCachePath()
            if (cacheDir.exists()) {
                Files.walk(cacheDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// desktop/src/main/kotlin/com/emgprosthetics/desktop/platform/SystemTray.kt
package com.emgprosthetics.desktop.platform

import java.awt.*
import java.awt.event.ActionListener
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class SystemTray {
    private var trayIcon: TrayIcon? = null
    private var isSupported = false

    init {
        isSupported = SystemTray.isSupported()
    }

    fun createTrayIcon(
        onShow: () -> Unit,
        onHide: () -> Unit,
        onExit: () -> Unit
    ): Result<Unit> {
        if (!isSupported) {
            return Result.failure(Exception("System tray not supported"))
        }

        return try {
            val tray = SystemTray.getSystemTray()

            // Create tray icon image
            val image = createTrayImage()

            // Create popup menu
            val popup = PopupMenu().apply {
                add(MenuItem("Show EMG Control Center").apply {
                    addActionListener { onShow() }
                })

                add(MenuItem("Hide to Tray").apply {
                    addActionListener { onHide() }
                })

                addSeparator()

                add(MenuItem("Settings").apply {
                    addActionListener { /* Open settings */ }
                })

                add(MenuItem("About").apply {
                    addActionListener { /* Show about dialog */ }
                })

                addSeparator()

                add(MenuItem("Exit").apply {
                    addActionListener { onExit() }
                })
            }

            // Create tray icon
            trayIcon = TrayIcon(image, "EMG Prosthetics Control Center", popup).apply {
                isImageAutoSize = true
                toolTip = "EMG Prosthetics Control Center"

                addActionListener { onShow() }
            }

            tray.add(trayIcon)
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun removeTrayIcon() {
        trayIcon?.let { icon ->
            SystemTray.getSystemTray().remove(icon)
            trayIcon = null
        }
    }

    fun showNotification(title: String, message: String, type: TrayIcon.MessageType = TrayIcon.MessageType.INFO) {
        trayIcon?.displayMessage(title, message, type)
    }

    fun updateIcon(isConnected: Boolean) {
        trayIcon?.let { icon ->
            val image = createTrayImage(isConnected)
            icon.image = image
        }
    }

    private fun createTrayImage(isConnected: Boolean = false): Image {
        // Create a simple 16x16 icon
        val size = 16
        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        try {
            // Enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            // Background
            g2d.color = Color(30, 30, 46) // Dark background
            g2d.fillRect(0, 0, size, size)

            // Main shape (simplified EMG wave)
            if (isConnected) {
                g2d.color = Color(0, 212, 255) // Primary blue
            } else {
                g2d.color = Color(128, 128, 128) // Gray when disconnected
            }

            // Draw simplified EMG signal pattern
            val stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            g2d.stroke = stroke

            val points = listOf(
                Point(2, 8), Point(4, 12), Point(6, 4),
                Point(8, 8), Point(10, 6), Point(12, 10), Point(14, 8)
            )

            for (i in 0 until points.size - 1) {
                g2d.drawLine(points[i].x, points[i].y, points[i + 1].x, points[i + 1].y)
            }

            // Status indicator dot
            if (isConnected) {
                g2d.color = Color(0, 255, 136) // Success green
                g2d.fillOval(12, 2, 3, 3)
            }

        } finally {
            g2d.dispose()
        }

        return image
    }

    fun isSystemTraySupported(): Boolean = isSupported
}