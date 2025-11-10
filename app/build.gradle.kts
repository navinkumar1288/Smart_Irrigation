
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.example.irrigctrl"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.irrigctrl"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}
dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material:material:1.6.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
}
