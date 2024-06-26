package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.FileManager;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.support.PluginSupport;
import com.badbones69.crazyauctions.api.support.MetricsWrapper;
import com.badbones69.crazyauctions.commands.AuctionCommand;
import com.badbones69.crazyauctions.commands.AuctionTab;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.controllers.MarcoListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
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

        FileConfiguration configuration = FileManager.Files.DATA.getFile();

        if (configuration.contains("OutOfTime/Cancelled")) {
            for (String key : configuration.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                final ItemStack itemStack = configuration.getItemStack("OutOfTime/Cancelled." + key + ".Item");

                if (itemStack != null) {
                    configuration.set("OutOfTime/Cancelled." + key + ".Item", Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));

                    FileManager.Files.DATA.saveFile();
                }

                final String uuid = configuration.getString("OutOfTime/Cancelled." + key + ".Seller");

                if (uuid != null) {
                    OfflinePlayer player = Methods.getOfflinePlayer(uuid);

                    configuration.set("OutOfTime/Cancelled." + key + ".Seller", player.getUniqueId().toString());

                    FileManager.Files.DATA.saveFile();
                }
            }
        }

        if (configuration.contains("Items")) {
            for (String key : configuration.getConfigurationSection("Items").getKeys(false)) {
                final ItemStack itemStack = configuration.getItemStack("Items." + key + ".Item");

                if (itemStack != null) {
                    configuration.set("Items." + key + ".Item", Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));

                    FileManager.Files.DATA.saveFile();
                }

                final String uuid = configuration.getString("Items." + key + ".Seller");

                if (uuid != null) {
                    OfflinePlayer player = Methods.getOfflinePlayer(uuid);

                    if (!uuid.equals(player.getUniqueId().toString())) {
                        configuration.set("Items." + key + ".Seller", player.getUniqueId().toString());

                        FileManager.Files.DATA.saveFile();
                    }
                }

                final String bidder = configuration.getString("Items." + key + ".TopBidder");

                if (bidder != null && !bidder.equals("None")) {
                    OfflinePlayer player = Methods.getOfflinePlayer(bidder);

                    if (!bidder.equals(player.getUniqueId().toString())) {
                        configuration.set("Items." + key + ".TopBidder", player.getUniqueId().toString());

                        FileManager.Files.DATA.saveFile();
                    }
                }
            }
        }

        this.crazyManager.load();

        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(new MarcoListener(), this);

        registerCommand(getCommand("crazyauctions"), new AuctionTab(), new AuctionCommand());

        // Run a task every 5 seconds to update auctions.
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                getServer().getGlobalRegionScheduler().run(get(), t -> Methods.updateAuction());
            }
        };

        this.timer.scheduleAtFixedRate(task, 20L, 5000L);

        // Add new messages.
        Messages.addMissingMessages();

        // Enable vault support if enabled.
        this.support = new VaultSupport();
        this.support.loadVault();

        // Create bstats instance.
        this.metrics = new MetricsWrapper(this, 4624);
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