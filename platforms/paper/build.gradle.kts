@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("crazyauctions.paper-plugin")

    alias(settings.plugins.minotaur)
}

repositories {
    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    api(project(":crazyauctions-api"))

    compileOnly(libs.paper)

    compileOnly(libs.crazycore.paper)

    compileOnly(libs.triumph.gui)
    compileOnly(libs.triumph.cmds)

    compileOnly(libs.config.me)

    compileOnly(libs.vault.api) {
        exclude("org.bukkit", "bukkit")
    }
}

val projectDescription = settings.versions.projectDescription.get()
val projectGithub = settings.versions.projectGithub.get()
val projectGroup = settings.versions.projectGroup.get()
val projectName = settings.versions.projectName.get()
val projectExt = settings.versions.projectExtension.get()

val isBeta = settings.versions.projectBeta.get().toBoolean()

val projectVersion = settings.versions.projectVersion.get()

val finalVersion = if (isBeta) "$projectVersion+Beta" else projectVersion

val type = if (isBeta) "beta" else "release"

tasks {
    shadowJar {
        archiveFileName.set("${projectName}+Paper+$finalVersion.jar")

        listOf(
            "org.bstats"
        ).forEach { relocate(it, "$projectGroup.library.$it") }
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(projectName.lowercase())

        versionName.set("$projectName $finalVersion")
        versionNumber.set(finalVersion)

        versionType.set(type)

        uploadFile.set(shadowJar.get())

        autoAddDependsOn.set(true)

        gameVersions.addAll(listOf("1.19, 1.19.1, 1.19.2, 1.19.3"))

        loaders.addAll(listOf("paper", "purpur"))

        //<h3>The first release for CrazyAuctions on Modrinth! 🎉🎉🎉🎉🎉<h3><br> If we want a header.
        changelog.set(
            """
                <h4>Changes:</h4>
                 <p>N/A</p>
                <h4>Under the hood changes</h4>
                 <p>N/A</p>
                <h4>Bug Fixes:</h4>
                 <p>N/A</p>
            """.trimIndent()
        )
    }

    processResources {
        filesMatching("paper-plugin.yml") {
            expand(
                "name" to projectName,
                "group" to projectGroup,
                "version" to finalVersion,
                "description" to projectDescription
            )
        }
    }
}

publishing {
    repositories {
        val repo = if (isBeta) "beta" else "releases"
        maven("https://repo.crazycrew.us/$repo") {
            name = "crazycrew"
            // Used for locally publishing.
            // credentials(PasswordCredentials::class)

            credentials {
                username = System.getenv("REPOSITORY_USERNAME")
                password = System.getenv("REPOSITORY_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = projectGroup
            artifactId = "${projectName.lowercase()}-${projectDir.name}"
            version = finalVersion

            from(components["java"])
        }
    }
}