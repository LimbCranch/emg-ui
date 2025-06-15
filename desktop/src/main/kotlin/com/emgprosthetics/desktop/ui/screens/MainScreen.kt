// desktop/src/main/kotlin/com/emgprosthetics/desktop/ui/screens/MainScreen.kt
package com.emgprosthetics.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.emgprosthetics.desktop.ui.components.*
import com.emgprosthetics.desktop.ui.theme.EmgColors
import com.emgprosthetics.ui.presentation.viewmodels.MainViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = koinInject()
) {
    val deviceState by viewModel.deviceState.collectAsState()
    val signalState by viewModel.signalState.collectAsState()
    val systemState by viewModel.systemState.collectAsState()

    // Main layout: Left panel, Center area, Right panel
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Left Panel - Device Control (300dp width)
        Card(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            DeviceControlPanel(
                modifier = Modifier.padding(16.dp),
                deviceState = deviceState,
                onStartDevice = viewModel::startDevice,
                onStopDevice = viewModel::stopDevice,
                onCalibrate = viewModel::startCalibration
            )
        }

        // Center Area - Signal Visualization
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main signal visualization (takes most space)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                SignalVisualizationPanel(
                    modifier = Modifier.padding(16.dp),
                    signalState = signalState
                )
            }

            // Gesture Recognition Panel (bottom 30%)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                GestureRecognitionPanel(
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Right Panel - System Monitoring (280dp width)
        Card(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            SystemMonitoringPanel(
                modifier = Modifier.padding(16.dp),
                systemState = systemState
            )
        }
    }
}

@Composable
fun DeviceControlPanel(
    modifier: Modifier = Modifier,
    deviceState: Any, // Will be properly typed later
    onStartDevice: () -> Unit,
    onStopDevice: () -> Unit,
    onCalibrate: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Device Control",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Device Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "EMG Device #1",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmgColors.Success)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Connected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Battery: 85%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Signal Quality: Good",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Control buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onStartDevice,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmgColors.Primary
                )
            ) {
                Text("Start Recording")
            }

            OutlinedButton(
                onClick = onStopDevice,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stop Recording")
            }

            Button(
                onClick = onCalibrate,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmgColors.Secondary
                )
            ) {
                Text("Calibrate")
            }
        }

        // Model Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Hand Model",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                var selectedModel by remember { mutableStateOf("Right Hand") }
                val models = listOf("Right Hand", "Left Hand", "Both Hands")

                models.forEach { model ->
                    Row {
                        RadioButton(
                            selected = selectedModel == model,
                            onClick = { selectedModel = model }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = model,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SignalVisualizationPanel(
    modifier: Modifier = Modifier,
    signalState: Any // Will be properly typed later
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "EMG Signal Visualization",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    onClick = { },
                    label = { Text("Ch1") },
                    selected = true
                )
                FilterChip(
                    onClick = { },
                    label = { Text("Ch2") },
                    selected = true
                )
                FilterChip(
                    onClick = { },
                    label = { Text("Ch3") },
                    selected = false
                )
                FilterChip(
                    onClick = { },
                    label = { Text("Ch4") },
                    selected = false
                )
            }
        }

        // Placeholder for real-time chart
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = EmgColors.Background,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                text = "Real-time EMG Signal Chart\n(Will be implemented with custom Canvas)",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Composable
fun GestureRecognitionPanel(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 3D Hand Model placeholder
        Column(
            modifier = Modifier.weight(0.6f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Hand Model",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = EmgColors.Background,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = "3D Hand Visualization\n(To be implemented)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Gesture predictions
        Column(
            modifier = Modifier.weight(0.4f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Predictions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val gestures = listOf(
                "Fist" to 0.85f,
                "Open Hand" to 0.12f,
                "Point" to 0.03f,
                "Rest" to 0.0f
            )

            gestures.forEach { (gesture, confidence) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = gesture,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${(confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (confidence > 0.7f) EmgColors.Success else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LinearProgressIndicator(
                    progress = confidence,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (confidence > 0.7f) EmgColors.Success else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun SystemMonitoringPanel(
    modifier: Modifier = Modifier,
    systemState: Any // Will be properly typed later
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "System Monitor",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Metrics cards
        val metrics = listOf(
            "CPU Usage" to "45%",
            "Memory" to "512 MB",
            "Signal Latency" to "12 ms",
            "Prediction Rate" to "50 Hz",
            "Frame Rate" to "60 FPS"
        )

        metrics.forEach { (name, value) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // System logs placeholder
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "System Logs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "[INFO] Device connected\n[INFO] Calibration complete\n[DEBUG] Signal quality: 0.92\n[INFO] Prediction: Fist (0.85)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}