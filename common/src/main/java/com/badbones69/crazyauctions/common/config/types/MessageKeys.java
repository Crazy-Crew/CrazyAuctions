package com.badbones69.crazyauctions.common.config.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
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

    public static final Property<String> page_header = newProperty("root.page.header", "<dark_gray>=========== <gold>CrazyAuctions {page}</gold> ===========");

    public static final Property<String> page_format = newProperty("root.page.format", " <gold>{command} <dark_gray>» <reset>{description}");

    public static final Property<String> page_footer = newProperty("root.page.footer", "<dark_gray>=========== <gold>CrazyAuctions {page}</gold> ===========");

    public static final Property<String> hover_format = newProperty("root.page.hover", "<gray>Click me to copy <gold>{command}");

    public static final Property<String> next_button = newProperty("root.page.next", "<green> >>>");

    public static final Property<String> navigation_text = newProperty("root.page.text", "<gray>Click me to go to the <gold>{type}</gold> page");

    public static final Property<String> back_button = newProperty("root.page.back", "<red> <<<");
}