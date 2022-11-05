plugins {
    kotlin("jvm") version "1.7.20"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")

val jenkinsVersion = "0.0.1-b$buildNumber"

group = "com.badbones69.crazyauctions"
version = "0.0.1"
description = "A simple auctions plugin where you can sell your items and bid on other items!"

repositories {
    mavenCentral()

    /**
     * Paper Team
     */
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib", "1.7.20"))

    compileOnly(libs.paper)
}

tasks {
    shadowJar {
        if (buildNumber != null) {
            archiveFileName.set("${rootProject.name}-[v${jenkinsVersion}].jar")
        } else {
            archiveFileName.set("${rootProject.name}-[v${rootProject.version}].jar")
        }

        //listOf(
        //    ""
        //).onEach {
        //    relocate(it, "${group}.libs.$it")
        //}
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}