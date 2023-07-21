plugins {
    id("root-plugin")
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazyauctions"
rootProject.description = "Auction off your items in style!"
rootProject.version = "2.0.0-rc1"

val combine by tasks.registering(Jar::class) {
    dependsOn("build")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(files(subprojects.map {
        it.layout.buildDirectory.file("libs/${rootProject.name}-${it.name}-${it.version}.jar").get()
    }).filter { it.name != "MANIFEST.MF" }.map { if (it.isDirectory) it else zipTree(it) })
}

allprojects {
    listOf(
        ":core",
        ":paper"
    ).forEach {
        project(it) {
            apply(plugin = "java")

            if (this.name == "paper") {
                dependencies {
                    compileOnly("de.tr7zw", "item-nbt-api", "2.11.3")
                    compileOnly("org.bstats", "bstats-bukkit", "3.0.2")

                    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
                }
            }

            //if (this.name == "core") {
            //    dependencies {
            //compileOnly("net.kyori", "adventure-api", "4.12.0")
            //compileOnly("net.kyori", "adventure-text-minimessage", "4.12.0")

            //compileOnly("com.google.code.gson", "gson", "2.10.1")
            //    }
            //}

            dependencies {
                //compileOnly("net.kyori", "adventure-platform-bukkit", "4.3.0")

                compileOnly("ch.jalu", "configme", "1.3.1")

                compileOnly("com.github.Carleslc.Simple-YAML", "Simple-Yaml", "1.8.4") {
                    exclude("org.yaml", "snakeyaml")
                }
            }
        }
    }
}

tasks {
    assemble {
        subprojects.forEach {
            dependsOn(":${it.project.name}:build")
        }

        finalizedBy(combine)
    }
}