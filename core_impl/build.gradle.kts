plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "ru.soft.core_impl"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            buildConfigField("String", "BASE_URL", "\"https://mis.clinicems.online:85/mis/ru_RU/hs/\"")
            buildConfigField("String", "USER", "\"apiuser\"")
            buildConfigField("String", "PASSWORD", "\"sG4@(dD63V&s!\"")
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"http://185.206.213.15:85/testmis/ru_RU/hs/\"")
            buildConfigField("String", "USER", "\"apiuser\"")
            buildConfigField("String", "PASSWORD", "\"sG4@(dD63V&s!\"")
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
}

dependencies {

    api(project(":core_api"))

    implementation(libs.okhttp.network)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.newtwork)
    implementation(libs.retrofit.scalars)
    implementation(libs.retrofit.gson)

    implementation(libs.cicerone.navigation)

    implementation(libs.dagger.di)
    kapt(libs.dagger.compiler)
}