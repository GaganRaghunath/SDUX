plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
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
    // Modules
    implementation(project(":domain"))
    implementation(project(":data"))

    // Core
    implementation(libs.androidx.core.ktx)

    // Lifecycle (runtime-ktx, runtime-compose, viewmodel-ktx)
    implementation(libs.bundles.lifecycle)

    // Coroutines (core + Android main-thread dispatcher)
    implementation(libs.bundles.coroutines)

    // Compose — BOM pins all artifact versions; bundle supplies the artifacts
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Hilt — DI runtime + hiltViewModel() + Navigation Compose integration
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)       // WorkManager + Navigation factory

    // Navigation 3
    implementation(libs.bundles.navigation3)

    // Serialization — runtime; compiler plugin generates $serializer for @Serializable classes
    implementation(libs.kotlinx.serialization.core)

    // WorkManager + Hilt Worker factory
    implementation(libs.bundles.work)

    // Unit tests
    testImplementation(project(":test-shared"))

    // Instrumented tests
    androidTestImplementation(libs.bundles.testing.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Debug tooling
    debugImplementation(libs.bundles.compose.debug)
}