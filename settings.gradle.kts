pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Product Explorer"
include(":app", ":core", ":domain", ":data")

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:testClasses"))
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:clean"))
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:assemble"))
 