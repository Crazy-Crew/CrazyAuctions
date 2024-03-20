enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "CrazyAuctions"

include(":paper")