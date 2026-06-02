plugins {
    `java-plugin`
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")

    api(project(":api"))

    implementation(libs.hikari.cp)

    //compileOnly(libs.bundles.adventure)
    compileOnly(libs.fusion.paper)
}