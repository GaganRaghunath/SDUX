plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.test.logger)
}

android {
    namespace = "com.anonymous.testshared"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 30
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// ── api() exposes all testing APIs transitively so consumers don't re-declare ──
dependencies {
    // JUnit 4 runner + rules
    api(libs.junit)

    // Coroutines — TestDispatcher, runTest, advanceUntilIdle
    api(libs.kotlinx.coroutines.test)

    // MockK — JVM mocking (unit tests)
    api(libs.mockk)

    // MockK — Android runtime mocking (instrumented tests)
    api(libs.mockk.android)

    // Turbine — Flow / StateFlow / SharedFlow assertions
    api(libs.turbine)

    // Kotest — rich assertion DSL (no runner, no engine)
    api(libs.kotest.assertions.core)

    // InstantTaskExecutorRule for LiveData / Arch components
    api(libs.androidx.arch.core.test)

    // ApplicationProvider + ActivityScenario for instrumented base
    api(libs.androidx.test.core.ktx)
}

testlogger {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA_PARALLEL
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = false
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showSimpleNames = false
    showPassed = true
    showSkipped = true
    showFailed = true
    showOnlySlow = false
    showStandardStreams = false
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
}
