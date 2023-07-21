package com.badbones69.crazyauctions;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import com.badbones69.crazyauctions.config.ConfigBuilder;
import com.badbones69.crazyauctions.config.types.PluginConfig;
import com.badbones69.crazyauctions.frame.utils.FileUtils;
import java.io.File;
import java.nio.file.Path;

public class ApiManager {

    private final Path path;

    public ApiManager(Path path) {
        this.path = path;
    }

    private static SettingsManager locale;
    private static SettingsManager config;
    private static SettingsManager pluginConfig;

    public void load() {
        File pluginConfigFile = new File(this.path.toFile(), "plugin-config.yml");

        pluginConfig = SettingsManagerBuilder
                .withYamlFile(pluginConfigFile)
                .useDefaultMigrationService()
                .configurationData(ConfigBuilder.buildPluginConfig())
                .create();

        File localeDir = new File(this.path.toFile(), "locale");
        FileUtils.extract("/locale/", this.path, false);

        File localeFile = new File(localeDir, pluginConfig.getProperty(PluginConfig.LOCALE_FILE) + ".yml");

        locale = SettingsManagerBuilder
                .withYamlFile(localeFile)
                .useDefaultMigrationService()
                .configurationData(ConfigBuilder.buildLocale())
                .create();

        // Create config.yml
        File configFile = new File(this.path.toFile(), "config.yml");

        config = SettingsManagerBuilder
                .withYamlFile(configFile)
                .useDefaultMigrationService()
                .configurationData(ConfigBuilder.buildConfig())
                .create();

    }

    public void reload() {
        // Reload configs.
        pluginConfig.reload();
        config.reload();

        locale.reload();

        File localeDir = new File(this.path.toFile(), "locale");
        File localeFile = new File(localeDir, pluginConfig.getProperty(PluginConfig.LOCALE_FILE) + ".yml");

        locale = SettingsManagerBuilder
                .withYamlFile(localeFile)
                .useDefaultMigrationService()
                .configurationData(ConfigBuilder.buildLocale())
                .create();
    }

    public static SettingsManager getPluginConfig() {
        return pluginConfig;
    }

    public static SettingsManager getLocale() {
        return locale;
    }

    public static SettingsManager getConfig() {
        return config;
    }
}