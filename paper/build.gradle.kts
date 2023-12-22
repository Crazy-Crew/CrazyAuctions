plugins {
    id("paper-plugin")
}

val mcVersion = rootProject.properties["minecraftVersion"] as String

dependencies {
    api(project(":common"))

    implementation(libs.bstats)

    compileOnly(fileTree("libs").include("*.jar"))
}

tasks {
    shadowJar {
        listOf(
            //"com.ryderbelserion.cluster.paper",
            //"de.tr7zw.changeme.nbtapi",
            //"dev.triumphteam.cmd",
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
            "apiVersion" to rootProject.properties["apiVersion"],
            "authors" to rootProject.properties["authors"],
            "website" to rootProject.properties["website"]
        )

        inputs.properties(properties)

        filesMatching("paper-plugin.yml") {
            expand(properties)
        }
    }
}