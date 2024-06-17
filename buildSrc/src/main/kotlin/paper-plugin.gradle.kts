import com.ryderbelserion.feather.enums.Repository

plugins {
    id("java-plugin")
}

repositories {
    maven("https://repo.triumphteam.dev/snapshots")

    maven("https://maven.enginehub.org/repo")

    maven(Repository.Paper.url)
}