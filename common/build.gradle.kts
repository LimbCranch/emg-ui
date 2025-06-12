// common/build.gradle.kts
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.20"
    id("com.android.library")
}

kotlin {
    jvm("desktop")
    android()

    // Uncomment when iOS support is added
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation("io.ktor:ktor-client-core:2.3.5")
                implementation("io.ktor:ktor-client-websockets:2.3.5")
                implementation("io.insert-koin:koin-core:3.5.0")

                // Logging
                implementation("io.github.oshai:kotlin-logging:5.1.0")

                // Configuration
                implementation("com.charleskorn.kaml:kaml:0.55.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
                implementation("io.mockk:mockk:1.13.8")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation("org.jetbrains.compose.desktop:desktop-jvm:1.5.4")
                implementation("io.ktor:ktor-client-okhttp:2.3.5")
                implementation("org.slf4j:slf4j-simple:2.0.9")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.activity:activity-compose:1.8.0")
                implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
                implementation("io.ktor:ktor-client-android:2.3.5")
                implementation("io.insert-koin:koin-android:3.5.0")
            }
        }
    }
}

android {
    namespace = "com.emgprosthetics.ui.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}