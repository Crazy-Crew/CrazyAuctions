package com.badbones69.crazyauctions.common;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyauctions.common.config.types.AuctionKeys;
import com.badbones69.crazyauctions.common.config.types.ConfigKeys;
import com.badbones69.crazyauctions.common.config.types.MessageKeys;
import java.io.File;

public class AuctionsFactory {

    private static SettingsManager config;

    private static SettingsManager auctions;
    private static SettingsManager messages;

    public void load(File dataFolder) {
        // Create yaml builder
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        // Create the config.yml
        config = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();

        // Create the config.yml
        auctions = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "auction-gui.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(AuctionKeys.class)
                .create();

        File localeDir = new File(dataFolder, "locale");

        if (!localeDir.exists()) localeDir.mkdirs();

        File file = new File(localeDir, config.getProperty(ConfigKeys.locale_file) + ".yml");

        messages = SettingsManagerBuilder
                .withYamlFile(file, builder)
                .useDefaultMigrationService()
                .configurationData(MessageKeys.class)
                .create();
    }

    public void reload() {
        // Reload the config.yml
        config.reload();

        // Reload auction-gui.yml
        auctions.reload();

        // Reload messages.yml
        messages.reload();
    }

    public static SettingsManager getConfig() {
        return config;
    }

    public static SettingsManager getAuctions() {
        return auctions;
    }

    public static SettingsManager getMessages() {
        return messages;
    }
}