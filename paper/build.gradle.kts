plugins {
    alias(libs.plugins.fix.javadoc)

    `maven-publish`
    `config-paper`
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.nexomc.com/releases/")

    maven("https://repo.oraxen.com/releases/")

    maven("https://maven.devs.beer/")
}

dependencies {
    implementation(libs.fusion.paper)

    implementation(libs.triumph.cmds)

    implementation(libs.nbt.api)

    implementation(libs.metrics)

    compileOnly(libs.bundles.shared)
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
    }

    runPaper.folia.registerTask()

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")
        jvmArgs("-Dcom.mojang.eula.agree=true")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }

    javadoc {
        val name = rootProject.name.replaceFirstChar { it.uppercase() }
        val options = options as StandardJavadocDocletOptions

        options.encoding = Charsets.UTF_8.name()
        options.overview("src/main/javadoc/overview.html")
        options.use()
        options.isDocFilesSubDirs = true
        options.windowTitle("$name ${rootProject.version} API Documentation")
        options.docTitle("<h1>$name ${rootProject.version} API</h1>")
        options.header = """<img src="https://raw.githubusercontent.com/Crazy-Crew/Branding/refs/heads/main/crazyvouchers/png/64x64.png" style="height:100%">"""
        options.bottom("Copyright © 2025 CrazyCrew")
        options.linkSource(true)
        options.addBooleanOption("html5", true)
    }

    withType<com.jeff_media.fixjavadoc.FixJavadoc> {
        configureEach {
            newLineOnMethodParameters.set(false)
            keepOriginal.set(false)
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.crazycrew.us/releases/")

            credentials(PasswordCredentials::class)
            authentication.create<BasicAuthentication>("basic")
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${rootProject.group}"
            artifactId = "${project.name}-api"
            version = rootProject.version.toString()
            from(components["java"])
        }
    }
}