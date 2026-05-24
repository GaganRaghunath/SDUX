// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.test.logger) apply false
    alias(libs.plugins.detekt) apply false
}

// ── Detekt — applied once here, covers every module ──────────────────────────
// libs accessor is not available inside subprojects {} (it resolves as a subproject
// extension, not the root project's type-safe accessor). Use VersionCatalog API instead.
val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        parallel = true
        autoCorrect = false                  // flip to true locally to auto-fix formatting
        baseline = rootProject.file("config/detekt/baseline.xml")
    }

    dependencies {
        // ktlint-backed formatting rules (indent, trailing comma, imports …)
        "detektPlugins"(catalog.findLibrary("detekt-formatting").get())
        // Twitter Compose static analysis rules
        "detektPlugins"(catalog.findLibrary("twitter-compose-rules").get())
    }
}