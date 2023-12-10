package com.badbones69.crazyauctions.common;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyauctions.common.config.types.ConfigKeys;
import java.io.File;

public class AuctionsFactory {

    private final File dataFolder;

    public AuctionsFactory(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    private SettingsManager config;

    public void load() {
        // Create yaml builder
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        // Create the config.yml
        this.config = SettingsManagerBuilder
                .withYamlFile(new File(this.dataFolder, "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();
    }

    public void reload() {
        // Reload the config.yml
        this.config.reload();
    }
}