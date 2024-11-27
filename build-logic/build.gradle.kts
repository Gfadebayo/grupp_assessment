plugins {
    `kotlin-dsl`
}

group = "com.grupp.assessment.buildlogic"

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "grupp.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "grupp.android.library"
            implementationClass = "plugin.LibraryPlugin"
        }
    }
}