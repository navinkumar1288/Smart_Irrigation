plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.smartirrigation"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smartirrigation"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.core:core:1.13.1")              // non-KTX
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // MQTT (Paho Java)
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
}