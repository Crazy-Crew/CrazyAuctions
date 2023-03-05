plugins {
    id("crazyauctions.root-plugin")
}

val projectBeta = settings.versions.projectBeta.get().toBoolean()
val projectVersion = settings.versions.projectVersion.get()
val projectName = settings.versions.projectName.get()

val finalVersion = if (projectBeta) "$projectVersion+Beta" else projectVersion

project.version = finalVersion

dependencies {
    //compileOnly(libs.adventure.api)
    //compileOnly(libs.adventure.text)

    compileOnly(libs.config.me)
    compileOnly(libs.yaml)

    compileOnly(libs.crazycore.api)
}

tasks {
    shadowJar {
        archiveFileName.set("$projectName+API+$finalVersion.jar")
    }
}