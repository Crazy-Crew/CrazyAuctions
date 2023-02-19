@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("crazyauctions.spigot-plugin")

    alias(settings.plugins.minotaur)
    alias(settings.plugins.run.paper)
}

repositories {
    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    /**
     * CrazyCrew Team
     */
    maven("https://repo.crazycrew.us/libraries")
}

dependencies {
//    api(project(":crazyauctions-core"))

    compileOnly(libs.spigot)

    implementation(libs.ruby.spigot)

    implementation(libs.triumph.gui)
    implementation(libs.triumph.cmds)

    implementation(libs.vault.api) {
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

val projectNameLowerCase = projectName.toLowerCase()

val repo = if (isBeta) "beta" else "releases"
val type = if (isBeta) "beta" else "release"

tasks {
    shadowJar {
        archiveFileName.set("${projectName}+$finalVersion.jar")

        listOf(
            "org.bstats"
        ).forEach { relocate(it, "$projectGroup.plugin.library.$it") }
    }

    runServer {
        minecraftVersion("1.19.3")
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(projectNameLowerCase)

        versionName.set("$projectName $finalVersion")
        versionNumber.set(finalVersion)

        versionType.set(type)

        uploadFile.set(shadowJar.get())

        autoAddDependsOn.set(true)

        gameVersions.addAll(
            listOf(
                "1.8",
                "1.9",
                "1.10",
                "1.11",
                "1.12",
                "1.13",
                "1.14",
                "1.15",
                "1.16",
                "1.17",
                "1.18",
                "1.19"
            )
        )

        loaders.addAll(listOf("spigot", "paper", "purpur"))

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
        filesMatching("plugin.yml") {
            expand(
                "name" to projectName,
                "group" to projectGroup,
                "version" to finalVersion,
                "description" to projectDescription,
                "website" to "https://modrinth.com/$projectExt/$projectNameLowerCase"
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = projectGroup
            artifactId = "$projectNameLowerCase-paper"
            version = finalVersion

            from(components["java"])

            pom {
                name.set(projectName)

                description.set(projectDescription)
                url.set(projectGithub)

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.opensource.org/licenses/mit-license.php")
                    }
                }

                developers {
                    developer {
                        id.set("ryderbelserion")
                        name.set("Ryder Belserion")
                    }

                    developer {
                        id.set("badbones69")
                        name.set("BadBones69")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/Crazy-Crew/$projectName.git")
                    developerConnection.set("scm:git:ssh://github.com/Crazy-Crew/$projectName.git")
                    url.set(projectGithub)
                }
            }
        }
    }

    repositories {
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
}