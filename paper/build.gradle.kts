plugins {
    id("paper-plugin")
}

val mcVersion = providers.gradleProperty("mcVersion").get()

dependencies {
    api(project(":common"))

    implementation(libs.cluster.paper)

    implementation(libs.commandapi)

    implementation(libs.metrics)

    compileOnly(libs.vault)

    compileOnly(fileTree("libs").include("*.jar"))
}

tasks {
    shadowJar {
        listOf(
            "dev.triumphteam.cmd",
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val properties = hashMapOf(
            "name" to rootProject.name,
            "version" to project.version,
            "group" to rootProject.group,
            "description" to rootProject.description,
            "apiVersion" to providers.gradleProperty("apiVersion").get(),
            "authors" to providers.gradleProperty("authors").get(),
            "website" to providers.gradleProperty("website").get()
        )

        inputs.properties(properties)

        filesMatching("paper-plugin.yml") {
            expand(properties)
        }
    }
}