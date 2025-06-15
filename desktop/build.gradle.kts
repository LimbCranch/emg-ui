// desktop/build.gradle.kts
plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

dependencies {
    implementation(project(":common"))

    // Compose Desktop
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")

    // Dependency Injection
    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("io.insert-koin:koin-compose:1.1.0")

    // Networking
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-okhttp:2.3.5")
    implementation("io.ktor:ktor-client-websockets:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-client-logging:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("io.github.oshai:kotlin-logging:5.1.0")

    // Configuration
    implementation("com.charleskorn.kaml:kaml:0.55.0")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
}

compose.desktop {
    application {
        mainClass = "com.emgprosthetics.desktop.MainKt"

        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )

            packageName = "EMG Prosthetics Control Center"
            packageVersion = "1.0.0"
            description = "EMG Prosthetics Control Center - Real-time signal monitoring and gesture recognition"
            copyright = "© 2024 EMG Prosthetics. All rights reserved."
            vendor = "EMG Prosthetics"

            windows {
                console = false
                dirChooser = true
                perUserInstall = true
                menuGroup = "EMG Prosthetics"

                iconFile.set(file("src/main/resources/icons/app-icon.ico"))
            }

            macOS {
                bundleID = "com.emgprosthetics.desktop"
                iconFile.set(file("src/main/resources/icons/app-icon.icns"))

                infoPlist {
                    extraKeysRawXml = """
                        <key>CFBundleDisplayName</key>
                        <string>EMG Control Center</string>
                        <key>LSRequiresIPhoneOS</key>
                        <false/>
                        <key>NSBluetoothAlwaysUsageDescription</key>
                        <string>This app needs Bluetooth access to connect to EMG devices.</string>
                    """
                }
            }

            linux {
                iconFile.set(file("src/main/resources/icons/app-icon.png"))
                packageName = "emg-prosthetics-control-center"
                debMaintainer = "emg-prosthetics@example.com"
                menuGroup = "Science"
                appCategory = "Science"
            }
        }

        buildTypes.release.proguard {
            configurationFiles.from("proguard-rules.pro")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        )
    }
}

// Task for running the application in development
tasks.register("runApp") {
    dependsOn("run")
    group = "application"
    description = "Run the EMG Prosthetics Desktop application"
}

// Task for creating a distributable package
tasks.register("packageApp") {
    dependsOn("packageDistributionForCurrentOS")
    group = "distribution"
    description = "Create a distributable package for the current OS"
}