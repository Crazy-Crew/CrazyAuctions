plugins {
    alias(libs.plugins.minotaur)
    alias(libs.plugins.hangar)

    `root-plugin`
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")

rootProject.version = if (buildNumber != null) "1.4.2-$buildNumber" else "1.4.2"

val isSnapshot = false

val content: String = rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)

subprojects.filter { it.name != "api" }.forEach {
    it.project.version = rootProject.version
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

    loaders.add("paper")
    loaders.add("purpur")

    autoAddDependsOn.set(false)
    detectLoaders.set(false)
}

hangarPublish {
    publications.register("plugin") {
        apiKey.set(System.getenv("HANGAR_KEY"))

        id.set(rootProject.name.lowercase())

        version.set(rootProject.version as String)

        channel.set(if (isSnapshot) "Snapshot" else "Release")

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