buildscript {
    val kotlin_version by extra("1.9.0")
    val agp_version2 by extra("8.3.0")
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.42")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }

        repositories {
            google()
            mavenCentral()
            maven(url = "https://jitpack.io")
        }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.0" apply false
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version ("2.0.1") apply false
    id ("org.jetbrains.kotlin.android") version ("1.9.0") apply false


}