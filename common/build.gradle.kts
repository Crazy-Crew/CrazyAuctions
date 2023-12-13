dependencies {
    compileOnlyApi(libs.bundles.adventure)

    compileOnly(libs.clusterApi5)

    api(libs.configme) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
}