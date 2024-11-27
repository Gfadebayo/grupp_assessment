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
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    buildFeatures {
        viewBinding = true
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

    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":domain"))

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation(libs.robolectric)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.gson)

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.navigation)

    implementation(libs.bundles.glide)

    implementation(libs.timber)

    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    testImplementation(libs.hilt.test)
    kspTest(libs.hilt.compiler)

    implementation(libs.bundles.glide)
    ksp(libs.glide.compiler)
}