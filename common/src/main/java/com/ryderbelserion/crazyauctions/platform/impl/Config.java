package com.ryderbelserion.crazyauctions.platform.impl;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import org.jetbrains.annotations.NotNull;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class Config implements SettingsHolder {


    @Override
    public void registerComments(@NotNull CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyAuctions/issues",
                "Features: https://github.com/Crazy-Crew/CrazyAuctions/issues",
                "",
                "Sounds: https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html",
                "Enchantments: https://jd.papermc.io/paper/1.20/org/bukkit/enchantments/Enchantment.html"
        };

        conf.setComment("root", header);
    }

    @Comment("Whether you want CrazyAuctions to shut up or not.")
    public static final Property<Boolean> verbose_logging = newProperty("root.verbose_logging", true);

    @Comment({
            "Sends anonymous statistics about how the plugin is used to bstats.org.",
            "bstats is a service for plugin developers to find out how the plugin being used,",
            "This information helps us figure out how to better improve the plugin."
    })
    public static final Property<Boolean> toggle_metrics = newProperty("root.toggle_metrics", true);

    @Comment("The prefix that appears in front of messages.")
    public static final Property<String> prefix = newProperty("root.prefix", "<dark_gray>[<blue>CrazyAuctions<dark_gray>]: ");
}