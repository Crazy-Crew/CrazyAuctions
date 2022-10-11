plugins {
    java

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")

val jenkinsVersion = "1.2.18-b$buildNumber"

group = "com.badbones69.crazyauctions"
version = "1.2.18"
description = "A simple auctions plugin where you can sell your items and bid on other items! "

repositories {
    /**
     * Paper Team
     */
    maven("https://repo.papermc.io/repository/maven-public/")

    /**
     * NBT Team
     */
    maven("https://repo.codemc.org/repository/maven-public/")

    /**
     * Everything else we need.
     */
    maven("https://jitpack.io/")

    mavenCentral()
}

dependencies {
    implementation(libs.bukkit.bstats)

    implementation(libs.nbt.api)

    compileOnly(libs.reserve.api)

    compileOnly(libs.vault.api)

    compileOnly(libs.paper)
}

tasks {
    shadowJar {
        if (buildNumber != null) {
            archiveFileName.set("${rootProject.name}-[v${jenkinsVersion}.jar")
        } else {
            archiveFileName.set("${rootProject.name}-[v${rootProject.version}].jar")
        }

        listOf(
            "de.tr7zw",
            "org.bstats"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }
    }

    compileJava {
        targetCompatibility = "8"
        sourceCompatibility = "8"
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description
            )
        }
    }
}