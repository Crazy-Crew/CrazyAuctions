import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    `config-paper`
}

project.group = "${rootProject.group}"

val git = feather.getGit()
val commitHash: String = git.getCurrentCommitHash().subSequence(0, 7).toString()
val isSnapshot: Boolean = git.getCurrentBranch() == "dev"
val content: String = if (isSnapshot) "[$commitHash](https://github.com/Crazy-Crew/${rootProject.name}/commit/$commitHash) ${git.getCurrentCommit()}" else rootProject.file("changelog.md").readText(Charsets.UTF_8)
val minecraft = libs.versions.minecraft.get()
val versions = listOf(minecraft)

repositories {
    maven("https://repo.nexomc.com/releases/")

    maven("https://repo.oraxen.com/releases/")

    maven("https://maven.devs.beer/")
}

dependencies {
    implementation(project(path = ":api"))

    implementation(libs.vital.paper) {
        exclude("org.yaml")
    }

    implementation(libs.fusion.paper)

    compileOnly(libs.bundles.shared)
}

modrinth {
    versionType = if (isSnapshot) "beta" else "release"

    changelog = content

    gameVersions.addAll(versions)

    uploadFile = tasks.shadowJar.get().archiveFile.get()

    loaders.addAll(listOf("paper", "folia", "purpur"))
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        listOf(
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }

        archiveBaseName.set("${rootProject.name}-${project.name.uppercaseFirstChar()}-${rootProject.version}")

        destinationDirectory.set(rootProject.layout.buildDirectory.get().dir("libs"))
    }

    runPaper.folia.registerTask()

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")
        jvmArgs("-Dcom.mojang.eula.agree=true")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}