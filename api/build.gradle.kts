plugins {
    alias(libs.plugins.fix.javadoc)

    `maven-publish`
    `config-java`
}

project.group = "us.crazycrew.crazyauctions"
project.description = "The official API for CrazyAuctions!"
project.version = "0.1.0"

dependencies {
    compileOnly(libs.fusion.core)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    javadoc {
        val name = rootProject.name.replaceFirstChar { it.uppercase() }
        val options = options as StandardJavadocDocletOptions

        options.encoding = Charsets.UTF_8.name()
        options.overview("src/main/javadoc/overview.html")
        options.use()
        options.isDocFilesSubDirs = true
        options.windowTitle("$name ${rootProject.version} API Documentation")
        options.docTitle("<h1>$name ${rootProject.version} API</h1>")
        options.header = """<img src="https://raw.githubusercontent.com/Crazy-Crew/Branding/refs/heads/main/crazyauctions/png/64x64.png" style="height:100%">"""
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
            groupId = "${project.group}" // us.crazycrew.crazyauctions
            artifactId = project.name
            version = "${project.version}"

            from(components["java"])
        }
    }
}