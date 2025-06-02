import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
//    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.kotlinSerialization)
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.shekharhandigol.aiarticlesummarizer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shekharhandigol.aiarticlesummarizer"
        minSdk = 26
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.firebase.crashlytics)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    //https://firebase.google.com/docs/ai-logic/get-started?platform=web&authuser=0&_gl=1*146x79*_ga*MTM5NTY2MjAyNS4xNzQ4ODI4Mzc1*_ga_CW55HF8NVT*czE3NDg4NDQzMDQkbzIkZzEkdDE3NDg4NDQ0NDckajQ0JGwwJGgw&api=dev#add-sdk

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)


    implementation(libs.jsoup)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.datastore.preferences)


    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    //noinspection KaptUsageInsteadOfKsp
    kapt(libs.androidx.room.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(libs.generativeai)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}