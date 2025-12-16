plugins {
    `paper-plugin`
}

project.group = "${rootProject.group}.paper"

repositories {
    maven("https://repo.nexomc.com/releases/")

    maven("https://repo.oraxen.com/releases/")

    maven("https://maven.devs.beer/")
}

dependencies {
    implementation(project(":api"))

    implementation(libs.vital.paper) {
        exclude("org.yaml")
    }

    compileOnly(libs.bundles.shared)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        listOf(
            "com.ryderbelserion.vital",
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
}