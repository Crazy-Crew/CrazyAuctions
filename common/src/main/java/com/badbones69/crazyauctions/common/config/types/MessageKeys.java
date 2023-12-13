package com.badbones69.crazyauctions.common.config.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MessageKeys implements SettingsHolder {

    protected MessageKeys() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew/CrazyAuctions",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyAuctions/issues",
                "Features: https://github.com/Crazy-Crew/CrazyAuctions/issues",
                "",
                "Tips:",
                " 1. Make sure to use the {prefix} to add the prefix in front of messages.",
                " 2. If you wish to use more than one line for a message just go from a line to a list.",
                "Examples:",
                "  Line:",
                "    plugin-reload: '{prefix}&cYou do not have permission to use that command.'",
                "  List:",
                "    help:",
                "      - '{prefix}&cYou do not have permission'",
                "      - '&cto use that command. Please try another.'"
        };

        conf.setComment("root", header);
    }

    public static final Property<String> plugin_reload = newProperty("root.plugin-reload", "{prefix}You have just reloaded the configurations.");

    public static final Property<List<String>> help = newListProperty("root.help", List.of(
            " <dark_red>Crazy<blue>Auctions <reset>Help",
            "",
            "  <dark_gray>- <gold>/ah help</gold> -></dark_gray> <reset>Lists all commands for CrazyAuctions.",
            "  <dark_gray>- <gold>/ah reload</gold> -></dark_gray> <reset>Reloads the plugin."
    ));
}