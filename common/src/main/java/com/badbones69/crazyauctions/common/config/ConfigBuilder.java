package com.badbones69.crazyauctions.common.config;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import com.badbones69.crazyauctions.common.config.types.Config;
import com.badbones69.crazyauctions.common.config.types.Messages;
import com.badbones69.crazyauctions.common.config.types.PluginConfig;

public class ConfigBuilder {

    private ConfigBuilder() {}

    public static ConfigurationData buildPluginConfig() {
        return ConfigurationDataBuilder.createConfiguration(
                PluginConfig.class
        );
    }

    public static ConfigurationData buildConfig() {
        return ConfigurationDataBuilder.createConfiguration(
                Config.class
        );
    }

    public static ConfigurationData buildLocale() {
        return ConfigurationDataBuilder.createConfiguration(
                Messages.class
        );
    }
}