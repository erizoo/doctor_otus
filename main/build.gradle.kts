plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "ru.soft.main"
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
}

dependencies {
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.design)
    implementation(libs.constraintlayout)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.lifecycle.extensions)

    implementation(libs.okhttp.network)
    implementation(libs.retrofit.newtwork)

    implementation(libs.security.crypto)
    implementation(libs.biometric.ktx)

    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.test.junit.ktx)
    androidTestImplementation(libs.espresso)

    implementation(libs.dagger.di)
    kapt(libs.dagger.compiler)
    api(project(":core"))
    implementation(libs.cicerone.navigation)
    implementation(project(":core_api"))
    implementation(project(":feature_auth_api"))
    implementation(project(":feature_home_api"))
    implementation(project(":feature_calendar_api"))
    implementation(project(":main_api"))
    implementation(project(":base_arch"))
}