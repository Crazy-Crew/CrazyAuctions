plugins {
    id("crazyauctions-common")
}

project.version = "${extra["plugin_version"]}"
project.group = "${extra["plugin_group"]}.CrazyAuctions"
project.description = "Auction your items off in style!"

repositories {
    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    /**
     * NBT Team
     */
    maven("https://repo.codemc.org/repository/maven-public/")

    /**
     * Paper Team
     */
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))

    implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-SNAPSHOT")

    implementation("com.ryderbelserion.ithildin", "ithildin-core", "0.0.1")

    implementation("org.bstats", "bstats-bukkit", "3.0.0")

    compileOnly("io.papermc.paper", "paper-api", "${project.extra["minecraft_version"]}-R0.1-SNAPSHOT")

    compileOnly("com.github.decentsoftware-eu", "decentholograms", "2.7.8")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")

    compileOnly("me.clip", "placeholderapi", "2.11.2") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }
}