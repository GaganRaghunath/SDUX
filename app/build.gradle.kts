plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.anonymous.sdux"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.anonymous.sdux"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)

    // Lifecycle (runtime-ktx, runtime-compose, viewmodel-ktx)
    implementation(libs.bundles.lifecycle)

    // Coroutines (core + Android main-thread dispatcher)
    implementation(libs.bundles.coroutines)

    // Compose — BOM pins all artifact versions; bundle supplies the artifacts
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Unit tests
    testImplementation(libs.bundles.testing)

    // Instrumented tests
    androidTestImplementation(libs.bundles.testing.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Debug tooling
    debugImplementation(libs.bundles.compose.debug)
}