plugins {
    id("paper-plugin")
}

dependencies {
    implementation(libs.metrics)

    compileOnly(libs.vault) {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly(fileTree("libs").include("*.jar"))
}

tasks {
    shadowJar {
        listOf(
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

        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
}