// Top-level build file where you can add configuration options common to all sub-projects/modules.
// build.gradle.kts (raíz del proyecto)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    id("com.google.dagger.hilt.android") version "2.52" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false

    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}
