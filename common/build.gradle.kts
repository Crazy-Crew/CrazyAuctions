dependencies {
    compileOnlyApi(libs.config) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    compileOnly(libs.cluster.api)
}