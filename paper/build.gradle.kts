plugins {
    `paper-plugin`

    id("io.papermc.paperweight.userdev")

    alias(libs.plugins.run.paper)
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://repo.oraxen.com/releases/")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.bundle)

    compileOnly(libs.placeholder.api)

    compileOnly(libs.oraxen.api)

    compileOnly(libs.vault)

    compileOnly(fileTree("libs").include("*.jar"))
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        downloadPlugins {
            url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")

            url("https://ci.lucko.me/job/TinyVault/lastSuccessfulBuild/artifact/build/libs/Vault.jar")

            url("https://download.luckperms.net/1532/bukkit/loader/LuckPerms-Bukkit-5.4.119.jar")
        }

        minecraftVersion("1.20.4")
    }

    shadowJar {
        //listOf(
            //"com.ryderbelserion.cluster",
            //"dev.triumphteam.cmd",
            //"dev.triumphteam.gui",
            //"org.bstats"
            //""
        //).forEach {
            //relocate(it, "libs.$it")
        //}
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