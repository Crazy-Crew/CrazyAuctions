plugins {
    id("root-plugin")

    id("com.github.johnrengelman.shadow")

    id("io.papermc.paperweight.userdev")
}

dependencies {
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}