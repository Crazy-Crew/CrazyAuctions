package us.crazycrew.crazyauctions;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.configs.PluginSettings;
import us.crazycrew.crazyauctions.configs.migrations.PluginMigrationService;
import us.crazycrew.crazycore.CrazyLogger;
import us.crazycrew.crazycore.paper.PaperConsole;
import us.crazycrew.crazycore.paper.PaperCore;
import us.crazycrew.crazycore.paper.player.PaperPlayerRegistry;
import java.io.File;
import java.util.logging.LogManager;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Created: 2/28/2023
 * Time: 1:25 AM
 * Last Edited: 2/28/2023 @ 3:13 AM
 *
 * Description: The starter class that thanks to paper is run directly at server startup and allows us to pass variables through the plugin class.
 */
@SuppressWarnings("UnstableApiUsage")
public class Starter implements PluginBootstrap {

    private PaperCore paperCore;

    private SettingsManager pluginConfig;

    @Override
    public void bootstrap(@NotNull PluginProviderContext context) {
        this.paperCore = new PaperCore(context.getConfiguration().getName(), context.getDataDirectory());

        this.pluginConfig = SettingsManagerBuilder
                .withYamlFile(new File(context.getDataDirectory().toFile(), "plugin-settings.yml"))
                .configurationData(PluginSettings.class)
                .migrationService(new PluginMigrationService()).create();
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        // Create the player registry.
        this.paperCore.setPaperPlayerRegistry(new PaperPlayerRegistry());

        // Create the console instance.
        this.paperCore.setPaperConsole(new PaperConsole());

        // Set the project prefix.
        this.paperCore.setProjectPrefix(getPluginConfig().getProperty(PluginSettings.CONSOLE_PREFIX));

        // Set the logger name and create it.
        CrazyLogger.setName(this.paperCore.getProjectName());

        // Add the logger manager.
        LogManager.getLogManager().addLogger(CrazyLogger.getLogger());

        return new CrazyAuctions(this.paperCore);
    }

    public SettingsManager getPluginConfig() {
        return this.pluginConfig;
    }
}