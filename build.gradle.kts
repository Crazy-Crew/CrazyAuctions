plugins {
    alias(libs.plugins.paperweight)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.runPaper)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.hangar)

    `paper-plugin`
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")

rootProject.version = if (buildNumber != null) "${libs.versions.minecraft.get()}-$buildNumber" else "1.6"

val isSnapshot = true

val content: String = rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)

    implementation(libs.vital.paper)

    compileOnly(libs.placeholderapi)

    compileOnly(libs.oraxen)

    compileOnly(libs.vault)

    compileOnly(fileTree("libs").include("*.jar"))
}

paperweight {
    reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION
}

tasks {
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        downloadPlugins {
            url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")

            url("https://ci.lucko.me/job/TinyVault/lastSuccessfulBuild/artifact/build/libs/Vault.jar")

            url("https://download.luckperms.net/1544/bukkit/loader/LuckPerms-Bukkit-5.4.131.jar")
        }

        minecraftVersion(libs.versions.minecraft.get())
    }

    assemble {
        dependsOn(reobfJar)

        doLast {
            copy {
                from(reobfJar.get())
                into(rootProject.projectDir.resolve("jars"))
            }
        }
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")

        listOf(
            "com.ryderbelserion"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        inputs.properties("name" to rootProject.name)
        inputs.properties("version" to project.version)
        inputs.properties("group" to project.group)
        inputs.properties("apiVersion" to libs.versions.minecraft.get())
        inputs.properties("description" to project.properties["description"])
        inputs.properties("authors" to project.properties["authors"])
        inputs.properties("website" to project.properties["website"])

        filesMatching("plugin.yml") {
            expand(inputs.properties)
        }
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))

        projectId.set(rootProject.name.lowercase())

        versionType.set(if (isSnapshot) "beta" else "release")

        versionName.set("${rootProject.name} ${rootProject.version}")
        versionNumber.set(rootProject.version as String)

        changelog.set(content)

        uploadFile.set(rootProject.projectDir.resolve("jars/${rootProject.name}-${rootProject.version}.jar"))

        gameVersions.set(listOf(libs.versions.minecraft.get()))

        loaders.addAll(listOf("purpur", "paper", "folia"))

        syncBodyFrom.set(rootProject.file("README.md").readText(Charsets.UTF_8))

        autoAddDependsOn.set(false)
        detectLoaders.set(false)
    }

    hangarPublish {
        publications.register("plugin") {
            apiKey.set(System.getenv("HANGAR_KEY"))

            id.set(rootProject.name.lowercase())

            version.set(rootProject.version as String)

            channel.set(if (isSnapshot) "Beta" else "Release")

            changelog.set(content)

            platforms {
                paper {
                    jar.set(rootProject.projectDir.resolve("jars/${rootProject.name}-${rootProject.version}.jar"))

                    platformVersions.set(listOf(libs.versions.minecraft.get()))

                    dependencies {
                        hangar("PlaceholderAPI") {
                            required = false
                        }
                    }
                }
            }
        }
    }
}