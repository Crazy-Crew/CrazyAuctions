dependencies {
    api(libs.config.me) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
}