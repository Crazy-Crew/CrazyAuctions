plugins {
    id("paper-plugin")
}

group = "${rootProject.group}.paper"
version = rootProject.version

base {
    archivesName = "${rootProject.name}-${project.name}"
}

repositories {
    //flatDir { dirs("libs") }

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    //maven("https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    implementation(project(":core"))

    //compileOnly(fileTree("libs").include("*.jar"))

    compileOnly("me.clip", "placeholderapi", "2.11.3")

    //compileOnly("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-SNAPSHOT")
    compileOnly("com.github.decentsoftware-eu", "decentholograms","2.8.3")
}

tasks {
    reobfJar {
        outputJar = file("$buildDir/libs/${rootProject.name}-${project.name}-${project.version}.jar")
    }

    processResources {
        filesMatching("paper-plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to project.group,
                "version" to project.version,
                "description" to rootProject.description
            )
        }
    }

    shadowJar {
        //listOf(
        //    "dev.jorel.commandapi"
        //).forEach {
        //    relocate(it, "libs.$it")
        //}
    }
}