package com.badbones69.crazyauctions.configs;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.configs.enums.Files;
import com.badbones69.crazyauctions.configs.impl.ConfigKeys;
import com.badbones69.crazyauctions.configs.impl.gui.AuctionKeys;
import com.badbones69.crazyauctions.configs.impl.locale.ErrorKeys;
import com.badbones69.crazyauctions.configs.impl.locale.GuiKeys;
import com.badbones69.crazyauctions.configs.impl.locale.MiscKeys;
import com.badbones69.crazyauctions.configs.impl.locale.PlayerKeys;
import com.ryderbelserion.vital.common.utils.FileUtil;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private static final Map<String, SettingsManager> locales = new HashMap<>();
    private static final Map<String, SettingsManager> configs = new HashMap<>();

    /**
     * Loads configuration files.
     */
    public static void load(final File dataFolder) {
        final File localeFolder = new File(dataFolder, "locale");

        YamlFileResourceOptions options = YamlFileResourceOptions.builder().indentationSize(2).build();

        for (Files file : Files.values()) {
            final String fileName = file.getFileName();

            final SettingsManagerBuilder builder = SettingsManagerBuilder.withYamlFile(file.getFile(), options).useDefaultMigrationService();

            switch (fileName) {
                case "auctions.yml" -> {
                    builder.configurationData(AuctionKeys.class);
                }

                case "config-v2.yml" -> {
                    builder.configurationData(ConfigKeys.class);
                }
            }

            configs.put(fileName, builder.create());
        }

        FileUtil.extracts(CrazyAuctions.class, "/locale/", dataFolder.toPath().resolve("locale"), false);

        final List<String> files = FileUtil.getFiles(dataFolder, "locale", ".yml");

        locales.put("en_US", SettingsManagerBuilder
                .withYamlFile(new File(localeFolder, "en_US.yml"), options)
                .useDefaultMigrationService()
                .configurationData(MiscKeys.class, PlayerKeys.class, ErrorKeys.class, GuiKeys.class)
                .create());

        files.forEach(file -> {
            if (!locales.containsKey(file)) {
                final SettingsManager settings = SettingsManagerBuilder
                        .withYamlFile(new File(localeFolder, file + ".yml"), options)
                        .useDefaultMigrationService()
                        .configurationData(MiscKeys.class, PlayerKeys.class, ErrorKeys.class, GuiKeys.class)
                        .create();

                locales.put(file, settings);
            }
        });
    }

    /**
     * Refreshes configuration files.
     */
    public static void refresh() {
        getAuctions().reload();
        getConfig().reload();

        locales.values().forEach(SettingsManager::reload);
    }

    /**
     * Gets the config instance of auctions.yml.
     *
     * @return {@link SettingsManager}
     */
    public static SettingsManager getAuctions() {
        return getCustomConfig(Files.auctions.getFileName());
    }

    /**
     * Gets the config instance of config-v2.yml.
     *
     * @return {@link SettingsManager}
     */
    public static SettingsManager getConfig() {
        return getCustomConfig(Files.config.getFileName());
    }

    /**
     * Gets the config instance of messages.yml.
     *
     * @return {@link SettingsManager}
     */
    public static SettingsManager getMessages() {
        return getCustomConfig("messages.yml");
    }

    /**
     * Gets {@link SettingsManager} from a file name
     *
     * @param fileName the name of the file
     * @return {@link SettingsManager}
     */
    public @NotNull static SettingsManager getCustomConfig(final String fileName) {
        return configs.get(fileName);
    }

    /**
     * Gets the locale for this identifier, or the default locale.
     *
     * @param locale the locale
     * @return {@link SettingsManager}
     */
    public static SettingsManager getLocale(final String locale) {
        if (getConfig().getProperty(ConfigKeys.per_player_locale)) {
            return locales.getOrDefault(getConfig().getProperty(ConfigKeys.default_locale_file), locales.get("en_US"));
        }

        return locales.getOrDefault(locale, locales.get("en_US"));
    }
}