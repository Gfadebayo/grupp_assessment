plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.kapt)
}

android {
    namespace = "com.grupp.assessment.productexplorer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.grupp.assessment.productexplorer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"")
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.nio)

//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.navigation)

    implementation(libs.bundles.glide)

    kapt("com.squareup.retrofit2:response-type-keeper:2.11.0")

    implementation(libs.timber)

    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    implementation(libs.bundles.network)

    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    implementation(libs.bundles.glide)
    ksp(libs.glide.compiler)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
}