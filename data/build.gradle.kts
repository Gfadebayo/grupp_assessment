plugins {
    id("grupp.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
}

android {
    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"")
    }

    buildFeatures {
        buildConfig = true
        resValues = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation(libs.robolectric)

    kapt("com.squareup.retrofit2:response-type-keeper:2.11.0")

    implementation(libs.gson)

    implementation(libs.bundles.network)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    testImplementation(libs.room.test)
}