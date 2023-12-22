plugins {
    id("root-plugin")
}

dependencies {
    compileOnlyApi(libs.bundles.adventure)

    compileOnly(libs.cluster.api)

    implementation(libs.cloud.core)

    api(libs.configme) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
}