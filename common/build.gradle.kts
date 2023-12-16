dependencies {
    compileOnlyApi(libs.bundles.adventure)

    compileOnly(libs.clusterApi5)

    implementation(libs.cloud.core)

    api(libs.configme) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
}