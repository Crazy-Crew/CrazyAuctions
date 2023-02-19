package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.economy.vault.VaultSupport;
import com.badbones69.crazyauctions.api.enums.PluginSupport;
import com.badbones69.crazyauctions.configs.Config;
import net.dehya.ruby.PaperRuby;
import net.dehya.ruby.RubyCore;
import net.dehya.ruby.command.RubyCommand;
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

    private final PaperRuby paperRuby = new PaperRuby(this);

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
    }

    @Override
    public void onDisable() {

    }

    @Override
    public @NotNull Path getDirectory() {
        return getDataFolder().toPath();
    }

    @Override
    public @NotNull Console getConsole() {
        return this.paperRuby.getConsole();
    }

    @Override
    public @NotNull RubyPlayerRegistry getPlayerRegistry() {
        return null;
    }

    @Override
    public @NotNull List<RubyCommand> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull String getPrefix() {
        return Config.PREFIX;
    }

    public static CrazyAuctions getPlugin() {
        return plugin;
    }

    public PaperRuby getPaperManager() {
        return this.paperRuby;
    }

    public PluginSupport getPluginSupport() {
        return this.pluginSupport;
    }

    public Starter getStarter() {
        return this.starter;
    }

    public VaultSupport getVaultSupport() {
        return this.starter.getVaultSupport();
    }
}