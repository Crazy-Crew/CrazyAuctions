package com.ryderbelserion.crazyauctions;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.ryderbelserion.crazyauctions.platform.impl.Config;
import com.ryderbelserion.crazyauctions.platform.Server;
import java.io.File;

public class CrazyAuctions {

    private final Server server;

    private final SettingsManager config;

    public CrazyAuctions(Server server) {
        this.server = server;


        // Create config files
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        this.config = SettingsManagerBuilder
                .withYamlFile(new File(server.getFolder(), "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(Config.class)
                .create();

        // Register provider.
        CrazyProvider.register(this);
    }

    public void reload() {
        // Reload the config.
        this.config.reload();
    }

    public void disable() {
        // Save the config.
        this.config.save();

        // Unregister provider.
        CrazyProvider.unregister();
    }

    public Server getServer() {
        return this.server;
    }

    public SettingsManager getConfig() {
        return this.config;
    }
}