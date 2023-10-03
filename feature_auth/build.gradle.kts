plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlinx.kover")
}

android {
    namespace = "ru.soft.feature_auth"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.design)
    implementation(libs.constraintlayout)

    api(project(":core"))
    implementation(project(":feature_auth_api"))
    implementation(project(":feature_home_api"))
    implementation(project(":main_api"))
    implementation(project(":ui_kit"))
    implementation(project(":ui_atoms"))
    implementation(project(":base_arch"))

    implementation(libs.navigation.fragment)
    implementation(libs.lifecycle.extensions)

    implementation(libs.okhttp.network)
    implementation(libs.retrofit.newtwork)
    implementation(libs.gson.google)
    implementation(libs.cicerone.navigation)

    implementation(libs.junit.jupiter)

    implementation(libs.dagger.di)
    kapt(libs.dagger.compiler)

}