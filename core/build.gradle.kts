plugins {
    id("paper-plugin")
}

group = "${rootProject.group}.core"
version = rootProject.version

base {
    archivesName = "${rootProject.name}-${project.name}"
}

tasks {
    assemble {
        dependsOn(shadowJar)
        doLast {
            delete(fileTree(baseDir = "$buildDir").include("**/*-dev*.jar"))
        }
    }

    shadowJar {
        archiveBaseName = "${rootProject.name}-${project.name}"
        archiveClassifier = ""
        mergeServiceFiles()
    }
}