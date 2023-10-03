plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlinx.kover")
    id("com.google.firebase.firebase-perf")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("../keystore/Untitled")
            storePassword = "mK9#S)kv2B3CpwX#"
            keyPassword = "mK9#S)kv2B3CpwX#"
            keyAlias = "key0"
        }
    }
    namespace = "ru.soft.med"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.soft.med"
        minSdk = 24
        targetSdk = 34
        versionCode = rootProject.extra["appVersionCode"] as Int
        versionName = rootProject.extra["appVersionName"] as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        extra["appVersionCode"] = versionCode
        extra["appVersionName"] = versionName
    }
    
    packaging {
        resources {
            excludes += "META-INF/*"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
        }
    }
    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName =
                    "MIS_${variant.baseName}_${variant.versionName}.apk"
                println("OutputFileName: $outputFileName")
                output.outputFileName = outputFileName
            }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            html.required.set(true)
        }
    }
    packaging {
        resources.pickFirsts.add("mockito-extensions/org.mockito.plugins.MemberAccessor")
        resources.pickFirsts.add("mockito-extensions/org.mockito.plugins.MockMaker")
    }
}

dependencies {

    implementation(libs.androidx.ktx)

    implementation(project(":core"))
    implementation(project(":main"))
    implementation(project(":base_arch"))
    implementation(project(":ui_kit"))
    implementation(project(":main_api"))
    implementation(project(":feature_auth"))
    implementation(project(":feature_auth_api"))
    implementation(project(":feature_home"))
    implementation(project(":feature_home_api"))
    implementation(project(":feature_calendar"))
    implementation(project(":feature_calendar_api"))

    implementation(libs.okhttp.network)
    implementation(libs.retrofit.newtwork)
    implementation(libs.cicerone.navigation)

    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.test.junit.ktx)
    androidTestImplementation(libs.kaspresso)
    androidTestImplementation(libs.mockito)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.mockito.inline)

    compileOnly(libs.detekt)

    implementation(libs.dagger.di)
    kapt(libs.dagger.compiler)

}