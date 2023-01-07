package com.badbones69.crazyauctions.configuration;

import com.ryderbelserion.ithildin.core.configs.AbstractYaml;
import java.nio.file.Path;

public class Config extends AbstractYaml {

    private static final Config CONFIG = new Config();

    @Key("settings.language-file")
    @Comment("""
            The language file to use from the locale folder.
            Supported languages are English(en).""")
    public static String LANGUAGE_FILE = "locale-en.yml";

    @Key("settings.verbose")
    @Comment("Whether you want to have verbose logging enabled.")
    public static boolean VERBOSE = true;

    @Key("settings.metrics")
    @Comment("Whether you want metrics to be enabled.")
    public static boolean METRICS = true;

    @Key("settings.updater")
    @Comment("""
            Sends you update notifications for when an update is available!
            You either need crazyauctions.command.admin.help or have /op
            """)
    public static boolean UPDATER = true;

    public static void handle(Path directory) {
        CONFIG.handle(directory.resolve("config.yml"), Config.class);
    }
}