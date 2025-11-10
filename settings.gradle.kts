pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        // AGP (Android Gradle Plugin)
        id("com.android.application") version "8.1.2"
        id("com.android.library")     version "8.1.2"
        // Kotlin plugin
        id("org.jetbrains.kotlin.jvm") version "1.9.20"
        id("org.jetbrains.kotlin.android") version "1.9.20"
    }
    resolutionStrategy {
        eachPlugin {
            // Optional: fallback mapping if plugin id used without explicit version
            when (requested.id.id) {
                "com.android.application", "com.android.library" -> useModule("com.android.tools.build:gradle:8.1.2")
                "org.jetbrains.kotlin.android", "org.jetbrains.kotlin.jvm" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Smart_Irrigation"
include(":app")
