package com.badbones69.crazyauctions.common.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

/**
 * Description: The plugin-settings.yml options.
 */
public class PluginConfig implements SettingsHolder {

    // Empty constructor required by SettingsHolder
    protected PluginConfig() {}

    @Comment({
            "Choose what language you want the plugin to be in.",
            "",
            "Available Languages: en-US"
    })
    public static final Property<String> LOCALE_FILE = PropertyInitializer.newProperty("language", "en-US");

    @Comment("Whether you want CrazyAuctions to shut up or not, This option is ignored by errors.")
    public static final Property<Boolean> verbose_logging = newProperty("verbose_logging", true);

    @Comment({
            "Sends anonymous statistics about how the plugin is used to bstats.org.",
            "bstats is a service for plugin developers to find out how the plugin being used,",
            "This information helps us figure out how to better improve the plugin."
    })
    public static final Property<Boolean> toggle_metrics = newProperty("toggle_metrics", true);
}