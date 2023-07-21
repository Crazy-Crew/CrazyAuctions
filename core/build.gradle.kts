plugins {
    id("paper-plugin")
}

group = "${rootProject.group}.core"
version = rootProject.version

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

tasks {
    assemble {
        dependsOn(shadowJar)
        doLast {
            delete(fileTree(baseDir = "$buildDir").include("**/*-dev*.jar"))
        }
    }

    shadowJar {
        archiveBaseName.set("${rootProject.name}-${project.name}")
        archiveClassifier.set("")
        mergeServiceFiles()
    }
}