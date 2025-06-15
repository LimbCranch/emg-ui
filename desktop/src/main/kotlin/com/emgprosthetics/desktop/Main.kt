// desktop/src/main/kotlin/com/emgprosthetics/desktop/Main.kt
package com.emgprosthetics.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.emgprosthetics.desktop.di.desktopModule
import com.emgprosthetics.desktop.ui.screens.MainScreen
import com.emgprosthetics.desktop.ui.theme.EmgProstheticsTheme
import org.koin.core.context.startKoin
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
fun main() = application {
    // Initialize dependency injection
    startKoin {
        modules(desktopModule)
    }

    val windowState = rememberWindowState(
        width = 1400.dp,
        height = 900.dp
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "EMG Prosthetics Control Center",
        state = windowState
    ) {
        EmgProstheticsTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainScreen()
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    EmgProstheticsTheme {
        MainScreen()
    }
}