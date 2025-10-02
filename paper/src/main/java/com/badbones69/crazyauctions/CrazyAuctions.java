package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.enums.Files;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.support.MetricsWrapper;
import com.badbones69.crazyauctions.commands.AuctionCommand;
import com.badbones69.crazyauctions.commands.AuctionTab;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.controllers.MacroListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.badbones69.crazyauctions.currency.CoinsEngineSupport;
import com.badbones69.crazyauctions.datafixer.ConfigFixer;
import com.ryderbelserion.vital.paper.Vital;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.Base64;

public class CrazyAuctions extends Vital {

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private CrazyManager crazyManager;
    private VaultSupport support;
    private CoinsEngineSupport supportCoinsEngine;  // Usage only in CrazyManager with check isCoinsEngineEnabled
    private boolean coinsEngineEnabled = false;


    @Override
    public void onEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            getLogger().severe("Vault was not found so the plugin will now disable.");

            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        getFileManager().addFile("config.yml")
                .addFile("data.yml")
                .addFile("messages.yml")
                //.addFile("test-file.yml")
                .init();

        FileConfiguration config = Files.config.getConfiguration();
        boolean configCoinsEnabled = config.getBoolean("Settings.CoinsEngineSupport.enable", false);

        if (configCoinsEnabled) {
            if (getServer().getPluginManager().isPluginEnabled("CoinsEngine")) {
                try {
                    this.supportCoinsEngine = new CoinsEngineSupport();
                    this.coinsEngineEnabled = true;
                    getLogger().info("CoinsEngine support enabled!");
                } catch (NoClassDefFoundError | Exception e) {
                    getLogger().warning("CoinsEngine found but failed to initialize: " + e.getMessage());
                    this.coinsEngineEnabled = false;
                }
            } else {
                getLogger().warning("CoinsEngine is enabled in config but plugin not found!");
                this.coinsEngineEnabled = false;
            }
        } else {
            this.coinsEngineEnabled = false;
        }

        this.crazyManager = new CrazyManager();
        this.crazyManager.load();

        FileConfiguration data = Files.data.getConfiguration();

        for (ConfigurationSection itemSection : this.crazyManager.getExpiredItems()) {
            final ItemStack itemStack = itemSection.getItemStack("Item");

            if (itemStack != null) {
                itemSection.set("Item", Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));

                Files.data.save();
            }

            final String uuid = itemSection.getString("Seller");

            if (uuid != null) {
                OfflinePlayer player = Methods.getOfflinePlayer(uuid);

                if (!uuid.equals(player.getUniqueId().toString())) {
                    itemSection.set("Seller", player.getUniqueId().toString());

                    Files.data.save();
                }
            }
        }

        for (ConfigurationSection itemSection : this.crazyManager.getItems()) {
            final ItemStack itemStack = itemSection.getItemStack("Item");

            if (itemStack != null) {
                itemSection.set("Item", Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));

                Files.data.save();
            }

            final String uuid = itemSection.getString("Seller");

            if (uuid != null) {
                OfflinePlayer player = Methods.getOfflinePlayer(uuid);

                if (!uuid.equals(player.getUniqueId().toString())) {
                    itemSection.set("Seller", player.getUniqueId().toString());

                    Files.data.save();
                }
            }

            final String bidder = itemSection.getString("TopBidder");

            if (bidder != null) {
                OfflinePlayer player = Methods.getOfflinePlayer(bidder);

                if (!bidder.equals(player.getUniqueId().toString())) {
                    itemSection.set("TopBidder", player.getUniqueId().toString());

                    Files.data.save();
                }
            }
        }

        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(new MacroListener(), this);

        registerCommand(getCommand("crazyauctions"), new AuctionTab(), new AuctionCommand());

        this.support = new VaultSupport();
        this.support.setupEconomy();

        new FoliaRunnable(getServer().getGlobalRegionScheduler()) {
            @Override
            public void run() {
                Methods.updateAuction();
            }
        }.runAtFixedRate(this, 0L, 5000L);

        Messages.addMissingMessages();
        new ConfigFixer().onEnable();

        new MetricsWrapper(this, 4624);
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    @Override
    public void onDisable() {
        if (this.crazyManager != null) this.crazyManager.unload();
    }

    public final VaultSupport getSupport() {
        return this.support;
    }

    public final CoinsEngineSupport getCoinsEngineSupport() {
        return this.supportCoinsEngine;
    }

    public final boolean isCoinsEngineEnabled() {
        return coinsEngineEnabled;
    }

    public final CrazyManager getCrazyManager() {
        return this.crazyManager;
    }
}