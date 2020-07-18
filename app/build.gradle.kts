plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(28)
    buildToolsVersion("29.0.3")

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)

        applicationId = "com.rizafu.sample"
        versionCode = 2
        versionName = "1.1"

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

dependencies {
    implementation(project(":coachmark"))
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("com.android.support:recyclerview-v7:28.0.0")
    implementation("com.android.support:cardview-v7:28.0.0")
    implementation("com.android.support:design:28.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")

//    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    testImplementation("junit:junit:4.13")
}
