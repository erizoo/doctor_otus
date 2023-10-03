// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.android.library") version "8.1.1" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.firebase.crashlytics") version "2.9.8" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.0" apply false
    id("org.jetbrains.kotlinx.kover") version "0.7.3" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
    id("io.github.detekt.gradle.compiler-plugin") version "1.23.1" apply false
    id("de.mannodermaus.android-junit5") version "1.9.3.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

allprojects {
    group = "io.gitlab.arturbosch.detekt"
    version = "1.23.1"

    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        buildUponDefaultConfig = true
        config.setFrom("${project.rootDir}/detekt-config.yml")
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "1.8"
        reports {
            xml.required = true
            html.required = true
            txt.required = true
            sarif.required = true
            md.required = true
        }
        basePath = rootDir.absolutePath
    }
    tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
        jvmTarget = "1.8"
    }
}

val versionMajor = 1
val versionMinor = 0
val versionPatch = 0
val versionBuild = 31

extra["appVersionCode"] = versionMajor * 10000 + versionMinor * 1000 + versionPatch * 100 + versionBuild
extra["appVersionName"] = "${versionMajor}.${versionMinor}.${versionPatch}.${versionBuild}"
