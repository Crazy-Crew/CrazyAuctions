package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.economy.vault.VaultSupport;
import com.badbones69.crazyauctions.api.enums.PluginSupport;
import com.badbones69.crazyauctions.configs.Config;
import com.badbones69.crazyauctions.configs.Locale;
import net.dehya.ruby.PaperRuby;
import net.dehya.ruby.RubyCore;
import net.dehya.ruby.SpigotRuby;
import net.dehya.ruby.command.cloud.RubyCommand;
import net.dehya.ruby.files.SpigotFileManager;
import net.dehya.ruby.player.RubyPlayerRegistry;
import net.dehya.ruby.registry.senders.types.Console;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class CrazyAuctions extends JavaPlugin implements RubyCore {

    private static CrazyAuctions plugin;

    private final SpigotRuby spigotRuby = new SpigotRuby(this);

    private final PluginSupport pluginSupport;

    private final Starter starter;

    public CrazyAuctions() {
        super();

        try {
            Field api = Provider.class.getDeclaredField("api");
            api.setAccessible(true);
            api.set(null, this);
        } catch (Exception e) {
            e.printStackTrace();

            getServer().getPluginManager().disablePlugin(this);
        }

        plugin = this;

        this.pluginSupport = new PluginSupport();

        this.starter = new Starter();

        this.pluginSupport.loadCurrency();
    }

    @Override
    public void onEnable() {
        Config.reload(this);
        Locale.reload(this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public @NotNull Path getDirectory() {
        return getDataFolder().toPath();
    }

    // ===================================================

    /**
     * These all can be null as we don't use them here.
     */

    @Override
    public @NotNull Console getConsole() {
        return null;
    }

    @Override
    public @NotNull RubyPlayerRegistry getPlayerRegistry() {
        return null;
    }

    @Override
    public @NotNull List<RubyCommand> getCommands() {
        return Collections.emptyList();
    }
    // ===================================================

    @Override
    public @NotNull String getPrefix() {
        return Config.PREFIX;
    }

    /**
     * @return the plugin instance
     */
    public static CrazyAuctions getPlugin() {
        return plugin;
    }

    /**
     * @return the spigot manager
     */
    public SpigotRuby getSpigotManager() {
        return this.spigotRuby;
    }

    /**
     * @return the spigot file manager
     */
    public SpigotFileManager getSpigotFileManager() {
        return this.spigotRuby.getSpigotFileManager();
    }

    /**
     * @return the plugin support class
     */
    public PluginSupport getPluginSupport() {
        return this.pluginSupport;
    }

    /**
     * @return the starter class
     */
    public Starter getStarter() {
        return this.starter;
    }

    /**
     * @return vault support
     */
    public VaultSupport getVaultSupport() {
        return this.starter.getVaultSupport();
    }

}