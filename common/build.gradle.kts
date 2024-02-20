plugins {
    id("root-plugin")
}

dependencies {
    compileOnlyApi(libs.bundles.adventure)

    compileOnly(libs.cluster.api)

    api(libs.config.me) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
}