// desktop/src/main/kotlin/com/emgprosthetics/desktop/di/DesktopModule.kt
package com.emgprosthetics.desktop.di

import com.emgprosthetics.ui.data.network.ApiClient
import com.emgprosthetics.ui.data.network.MessageBrokerClient
import com.emgprosthetics.ui.data.repositories.ConfigRepository
import com.emgprosthetics.ui.data.repositories.EmgDataRepository
import com.emgprosthetics.ui.domain.usecases.CalibrateSystemUseCase
import com.emgprosthetics.ui.domain.usecases.ConnectDeviceUseCase
import com.emgprosthetics.ui.domain.usecases.MonitorSignalUseCase
import com.emgprosthetics.ui.presentation.viewmodels.CalibrationViewModel
import com.emgprosthetics.ui.presentation.viewmodels.MainViewModel
import com.emgprosthetics.ui.presentation.viewmodels.MonitoringViewModel
import com.emgprosthetics.desktop.platform.BluetoothManager
import com.emgprosthetics.desktop.platform.FileSystem
import com.emgprosthetics.desktop.platform.SystemTray
import org.koin.dsl.module
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*

val desktopModule = module {

    // HTTP Client
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            install(WebSockets) {
                pingInterval = 20_000
            }
        }
    }

    // Network layer
    single { ApiClient(get()) }
    single { MessageBrokerClient(get()) }

    // Platform specific
    single { BluetoothManager() }
    single { FileSystem() }
    single { SystemTray() }

    // Repositories
    single<ConfigRepository> { MockConfigRepository() }
    single<EmgDataRepository> { MockEmgDataRepository() }

    // Use Cases
    single { ConnectDeviceUseCase(get()) }
    single { CalibrateSystemUseCase(get()) }
    single { MonitorSignalUseCase(get()) }

    // ViewModels
    single { MainViewModel(get(), get(), get()) }
    single { CalibrationViewModel(get()) }
    single { MonitoringViewModel(get()) }
}