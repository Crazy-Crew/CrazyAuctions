package com.badbones69.crazyauctions.configuration;

import com.ryderbelserion.ithildin.core.configs.AbstractYaml;
import com.ryderbelserion.ithildin.core.utils.FileUtils;
import java.nio.file.Path;

public class Locale extends AbstractYaml {

    private static final Locale LOCALE = new Locale();

    @Key("prefix.command")
    @Comment("Change how the prefix for commands will look!")
    public static String COMMAND_PREFIX = "<gray>[<gradient:#da184d:#b8c9e6>CrazyAuctions</gradient>]</gray>";

    @Key("prefix.console")
    @Comment("Change how the prefix for console will look!")
    public static String CONSOLE_PREFIX = "<gray>[<gradient:#da184d:#b8c9e6>CrazyAuctions</gradient>]</gray>";

    @Key("misc.unknown-command")
    public static String UNKNOWN_COMMAND = "<red>This command is not known.</red>";

    @Key("misc.config-reload")
    public static String CONFIG_RELOAD = "<red>You have reloaded the plugin.</red>";

    public static void handle(Path directory) {
        FileUtils.extract("/locale/", directory, false);

        LOCALE.handle(directory.resolve(Config.LANGUAGE_FILE), Locale.class);
    }
}