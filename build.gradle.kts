plugins {
    id("root-plugin")
}

rootProject.group = "com.ryderbelserion.crazyauctions"

val buildNumber: String? = System.getenv("BUILD_NUMBER")
val isPublishing: String? = System.getenv("IS_PUBLISHING")

rootProject.version = if (buildNumber != null && isPublishing == null) "${libs.versions.minecraft.get()}-$buildNumber" else rootProject.properties["version"].toString()