plugins {
    id("root-plugin")
}

dependencies {
    compileOnlyApi(libs.bundles.adventure)

    compileOnly(libs.cluster.api)

    api(libs.configme) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
}