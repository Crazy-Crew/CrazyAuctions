package com.badbones69.crazyauctions.common;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyauctions.common.config.types.AuctionKeys;
import com.badbones69.crazyauctions.common.config.types.ConfigKeys;
import java.io.File;

public class AuctionsFactory {

    private SettingsManager config;

    private SettingsManager auctions;

    public void load(File dataFolder) {
        // Create yaml builder
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        // Create the config.yml
        this.config = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();

        // Create the config.yml
        this.auctions = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "auction-gui.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(AuctionKeys.class)
                .create();
    }

    public void reload() {
        // Reload the config.yml
        this.config.reload();

        // Reload auction-gui.yml
        this.auctions.reload();
    }

    public SettingsManager getConfig() {
        return this.config;
    }

    public SettingsManager getAuctions() {
        return this.auctions;
    }
}