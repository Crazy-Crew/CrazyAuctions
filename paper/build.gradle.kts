plugins {
    `paper-plugin`
}

project.group = "${rootProject.group}"

repositories {
    exclusiveContent {
        forRepository {
            maven("https://repo.essentialsx.net/releases/")
        }

        filter {
            includeGroup("net.essentialsx")
        }
    }

    maven("https://repo.fancyinnovations.com/releases/")

    maven("https://repo.momirealms.net/releases/")

    maven("https://repo.hibiscusmc.com/releases/")

    maven("https://repo.nexomc.com/releases/")

    maven("https://repo.oraxen.com/releases/")
}

dependencies {
    api(project(":common"))

    implementation(libs.fusion.paper)

    implementation(libs.metrics)

    compileOnly(libs.bundles.shared)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        listOf(
            "com.ryderbelserion.fusion",
            "io.leangen.geantyref",
            "dev.triumphteam.cmd",
            "com.zaxxer.hikari",
            "org.spongepowered",
            "com.google.gson",
            "org.jspecify",
            "org.bstats",
            "org.slf4j",
            "org.yaml"
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