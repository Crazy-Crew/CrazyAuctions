@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("crazyauctions.paper-plugin")

    alias(settings.plugins.minotaur)
    alias(settings.plugins.run.paper)
}

repositories {
    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    api(project(":crazyauctions-api"))

    compileOnly(libs.crazycore.paper)

    compileOnly(libs.triumph.gui)
    compileOnly(libs.triumph.cmds)

    compileOnly(libs.config.me)

    compileOnly(libs.vault.api) {
        exclude("org.bukkit", "bukkit")
    }
}

val github = settings.versions.github.get()
val extension = settings.versions.extension.get()

val beta = settings.versions.beta.get().toBoolean()

val type = if (beta) "beta" else "release"

tasks {
    shadowJar {
        fun reloc(pkg: String) = relocate(pkg, "${rootProject.group}.dependency.$pkg")

        reloc("de.tr7zw.changeme.nbtapi")
        reloc("org.bstats")
        reloc("dev.triumphteam.cmd")
    }

    runServer {
        minecraftVersion("1.19.4")
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(rootProject.name.lowercase())

        versionName.set("${rootProject.name} ${rootProject.version}")
        versionNumber.set(rootProject.version.toString())

        versionType.set(type)

        uploadFile.set(shadowJar.get())

        autoAddDependsOn.set(true)

        gameVersions.addAll(
            listOf(
                "1.19",
                "1.19.1",
                "1.19.2",
                "1.19.3",
                "1.19.4"
            )
        )

        loaders.addAll(listOf("paper", "purpur"))

        //<h3>The first release for CrazyCrates on Modrinth! 🎉🎉🎉🎉🎉<h3><br> If we want a header.
        changelog.set(
            """
                <h4>Changes:</h4>
                 <p>Added 1.19.4 support</p>
                 <p>Removed 1.18.2 and below support</p>
                <h4>Under the hood changes</h4>
                 <p>Simplified build script</p>
                <h4>Bug Fixes:</h4>
                 <p>N/A</p>
            """.trimIndent()
        )
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description,
                "website" to "https://modrinth.com/$extension/${rootProject.name.lowercase()}"
            )
        }
    }
}

publishing {
    repositories {
        val repo = if (beta) "beta" else "releases"
        maven("https://repo.crazycrew.us/$repo") {
            name = "crazycrew"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = "${rootProject.name.lowercase()}-api"
            version = rootProject.version.toString()

            from(components["java"])
        }
    }
}