
plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(28)
    buildToolsVersion("29.0.3")

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 11
        versionName = "1.2.2"
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

    }


    lintOptions{
        isAbortOnError = false
    }

    buildFeatures{
        dataBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }
}

repositories {
    jcenter()
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.support:support-v4:28.0.0")
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")

//    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    testImplementation("junit:junit:4.13")
}
