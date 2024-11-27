package  plugin

import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

class LibraryPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("com.android.library")

            extensions.getByType<LibraryExtension>().apply {
                namespace = "com.grupp.assessment.productexplorer.${name}"
                compileSdk = 35

                defaultConfig {
                    minSdk = 24

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                    consumerProguardFiles += listOf(file("consumer-rules.pro"))
                }

                compileOptions {
                    isCoreLibraryDesugaringEnabled = name != "core"

                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                buildFeatures {
                    aidl = false
                    buildConfig = false
                    resValues = false
                }

                (this as ExtensionAware).configure<KotlinJvmOptions> {
                    jvmTarget = "17"
                }
            }

            dependencies {
                if(name != "core") {
                    add("implementation", project(":core"))

                    if(name != "domain") add("implementation", project(":domain"))

                    add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.0.4")
                }

                add("implementation", fileTree("libs"))

                add("implementation", "com.jakewharton.timber:timber:5.0.1")
            }
        }
    }
}