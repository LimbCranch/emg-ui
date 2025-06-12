// build.gradle.kts (root)
plugins {
    kotlin("multiplatform") version "1.9.20" apply false
    kotlin("android") version "1.9.20" apply false
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.compose") version "1.5.4" apply false
    id("org.jetbrains.dokka") version "1.9.10"

}

dokka {
    outputDirectory.set(file("$buildDir/dokka"))

    dokkaSourceSets {
        named("commonMain") {
            displayName.set("Common")

            sourceLink {
                localDirectory.set(file("src/commonMain/kotlin"))
                remoteUrl.set(URL("https://github.com/emg-prosthetics/emg-ui/tree/main/common/src/commonMain/kotlin"))
                remoteLineSuffix.set("#L")
            }

            perPackageOption {
                matchingRegex.set(".*\\.internal.*")
                suppress.set(true)
            }

            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/latest/jvm/stdlib/"))
            }
        }
    }
}


tasks.register("generateApiDocs") {
    dependsOn("dokkaHtml")

    doLast {
        copy {
            from("$buildDir/dokka")
            into("../emg-docs/docs/api/kotlin/")
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

subprojects {
    apply(plugin = "org.gradle.idea")

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs += listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        }
    }
}