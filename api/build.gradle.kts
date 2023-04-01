plugins {
    id("crazyauctions.root-plugin")
}

dependencies {
    //compileOnly(libs.adventure.api)
    //compileOnly(libs.adventure.text)

    compileOnly(libs.config.me)
    compileOnly(libs.yaml)

    compileOnly(libs.crazycore.api)
}