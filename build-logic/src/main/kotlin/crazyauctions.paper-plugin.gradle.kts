plugins {
    id("crazyauctions.root-plugin")
}

repositories {
    exclusiveContent {
        forRepository {
            maven("https://repo.papermc.io/repository/maven-public/")
        }

        filter {
            includeGroup("io.papermc.paper")
            includeGroup("com.mojang")
            includeGroup("net.md-5")
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(project.properties["java_version"].toString()))
}

tasks {
    compileJava {
        options.release.set(project.properties["java_version"].toString().toInt())
    }
}