package us.crazycrew.crazyauctions.loader;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.CrazyAuctions;
import us.crazycrew.crazyauctions.configurations.ConfigSettings;
import us.crazycrew.crazyauctions.configurations.LocaleSettings;
import us.crazycrew.crazyauctions.configurations.PluginSettings;
import us.crazycrew.crazyauctions.configurations.migrations.PluginMigrationService;
import us.crazycrew.crazycore.paper.PaperCore;
import us.crazycrew.crazycore.utils.FileUtils;

import java.io.File;

/**
 * Description: The starter class that thanks to paper is run directly at server startup and allows us to pass variables through the plugin class.
 */
@SuppressWarnings("UnstableApiUsage")
public class AuctionsStarter implements PluginBootstrap {

    private PaperCore paperCore;

    private static SettingsManager pluginConfig;
    private static SettingsManager config;
    private static SettingsManager locale;

    @Override
    public void bootstrap(@NotNull PluginProviderContext context) {
        this.paperCore = new PaperCore(context.getDataDirectory());

        pluginConfig = SettingsManagerBuilder
                .withYamlFile(new File(context.getDataDirectory().toFile(), "plugin-settings.yml"))
                .configurationData(PluginSettings.class)
                .migrationService(new PluginMigrationService()).create();

        config = SettingsManagerBuilder
                .withYamlFile(new File(context.getDataDirectory().toFile(), "config.yml"))
                .configurationData(ConfigSettings.class)
                .create();
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        FileUtils.extract("/locale", context.getDataDirectory(), false);

        locale = SettingsManagerBuilder
                .withYamlFile(new File(context.getDataDirectory().toFile() + "/locale/", pluginConfig.getProperty(PluginSettings.LOCALE_FILE)))
                .configurationData(LocaleSettings.class)
                .create();

        return new CrazyAuctions(this.paperCore);
    }

    public static SettingsManager getPluginConfig() {
        return pluginConfig;
    }

    public static SettingsManager getConfig() {
        return config;
    }

    public static SettingsManager getLocale() {
        return locale;
    }
}