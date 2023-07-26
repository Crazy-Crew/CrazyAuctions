pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")

        maven("https://maven.minecraftforge.net/")

        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "CrazyAuctions"

include("core", "paper")