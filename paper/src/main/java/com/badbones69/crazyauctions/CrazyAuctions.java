package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.FileManager;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.support.PluginSupport;
import com.badbones69.crazyauctions.api.support.metrics.MetricsWrapper;
import com.badbones69.crazyauctions.commands.AuctionCommand;
import com.badbones69.crazyauctions.commands.AuctionTab;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.controllers.MarcoListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.Timer;
import java.util.TimerTask;

public class CrazyAuctions extends JavaPlugin {

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private Timer timer;

    private FileManager fileManager;
    private CrazyManager crazyManager;

    private VaultSupport support;

    private MetricsWrapper metrics;

    @Override
    public void onEnable() {
        if (!PluginSupport.VAULT.isPluginEnabled()) {
            getLogger().severe("Vault was not found so the plugin will now disable.");

            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        this.timer = new Timer();

        this.fileManager = new FileManager();
        this.crazyManager = new CrazyManager();

        this.fileManager.setup();
        this.crazyManager.load();

        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(new MarcoListener(), this);

        registerCommand(getCommand("crazyauctions"), new AuctionTab(), new AuctionCommand());

        // Run a task every 5 seconds to update auctions.
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                getServer().getScheduler().runTask(get(), Methods::updateAuction);
            }
        };

        this.timer.scheduleAtFixedRate(task, 20L, 5000L);

        // Add new messages.
        Messages.addMissingMessages();

        // Enable vault support if enabled.
        this.support = new VaultSupport();
        support.loadVault();

        // Create bstats instance.
        this.metrics = new MetricsWrapper();
        this.metrics.start();
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    @Override
    public void onDisable() {
        if (this.timer != null) this.timer.cancel();

        if (this.crazyManager != null) this.crazyManager.unload();
    }

    @NotNull
    public Timer getTimer() {
        return this.timer;
    }

    public VaultSupport getSupport() {
        return this.support;
    }

    public MetricsWrapper getMetrics() {
        return this.metrics;
    }

    @NotNull
    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    @NotNull
    public FileManager getFileManager() {
        return this.fileManager;
    }

    public boolean isLogging() {
        return true;
    }
}