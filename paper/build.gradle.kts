plugins {
    id("paper-plugin")
}

group = "${rootProject.group}.paper"
version = rootProject.version

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

dependencies {
    implementation(project(":core"))
}

tasks {
    processResources {
        val props = mapOf(
            "name" to rootProject.name,
            "group" to project.group,
            "version" to project.version,
            "description" to rootProject.description,
            "apiVersion" to "1.20"
        )

        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}