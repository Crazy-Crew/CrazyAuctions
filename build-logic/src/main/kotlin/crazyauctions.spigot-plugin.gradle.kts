import gradle.kotlin.dsl.accessors._8aa390c341e35d66485a63bc7873c757.java
import org.gradle.api.JavaVersion

plugins {
    id("crazyauctions.root-plugin")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}