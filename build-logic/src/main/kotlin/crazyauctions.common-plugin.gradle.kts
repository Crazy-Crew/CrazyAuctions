plugins {
    `java-library`
}

repositories {
    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.crazycrew.us/libraries/")

    maven("https://repo.crazycrew.us/plugins/")

    maven("https://libraries.minecraft.net/")

    maven("https://jitpack.io/")

    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(extra["java_version"].toString().toInt()))
}

tasks {
    compileJava {
        options.release.set(extra["java_version"].toString().toInt())
    }
}