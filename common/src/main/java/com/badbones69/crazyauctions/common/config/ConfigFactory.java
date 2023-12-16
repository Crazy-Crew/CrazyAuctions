package com.badbones69.crazyauctions.common.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import com.badbones69.crazyauctions.common.api.CrazyAuctionsPlugin;
import com.badbones69.crazyauctions.common.api.interfaces.AbstractPlugin;
import com.badbones69.crazyauctions.common.config.types.Config;
import com.badbones69.crazyauctions.common.config.types.Locale;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public class ConfigFactory {

    @NotNull
    private final AbstractPlugin plugin = CrazyAuctionsPlugin.get();

    private SettingsManager config;
    private SettingsManager messages;

    public void load() {
        this.config = SettingsManagerBuilder
                .withYamlFile(new File(this.plugin.getDataFolder(), "config.yml"), this.plugin.indent())
                .useDefaultMigrationService()
                .configurationData(Config.class)
                .create();

        File directory = new File(this.plugin.getDataFolder(), "locale");

        if (!directory.exists()) directory.mkdirs();

        File locale = new File(directory, config.getProperty(Config.locale_file) + ".yml");

        this.messages = SettingsManagerBuilder
                .withYamlFile(locale, this.plugin.indent())
                .useDefaultMigrationService()
                .configurationData(Locale.class)
                .create();
    }

    public void reload() {
        // Reload the config.yml
        this.config.reload();

        // Reload messages.yml
        this.messages.reload();
    }

    public SettingsManager getConfig() {
        return this.config;
    }

    public SettingsManager getMessages() {
        return this.messages;
    }
}