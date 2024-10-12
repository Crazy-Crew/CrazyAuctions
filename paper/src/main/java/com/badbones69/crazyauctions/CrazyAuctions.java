package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.api.guis.types.AuctionsMenu;
import com.badbones69.crazyauctions.api.guis.types.CategoriesMenu;
import com.badbones69.crazyauctions.api.guis.types.CurrentMenu;
import com.badbones69.crazyauctions.api.guis.types.ExpiredMenu;
import com.badbones69.crazyauctions.api.guis.types.other.BidMenu;
import com.badbones69.crazyauctions.api.guis.types.other.BuyingMenu;
import com.badbones69.crazyauctions.api.support.MetricsWrapper;
import com.badbones69.crazyauctions.commands.AuctionCommand;
import com.badbones69.crazyauctions.commands.AuctionTab;
import com.badbones69.crazyauctions.controllers.MarcoListener;
import com.badbones69.crazyauctions.controllers.MiscListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.badbones69.crazyauctions.tasks.InventoryManager;
import com.ryderbelserion.vital.paper.Vital;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public class CrazyAuctions extends Vital {

    public @NotNull static CrazyAuctions getPlugin() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private CrazyManager crazyManager;

    private VaultSupport support;

    @Override
    public void onEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            getLogger().severe("Vault was not found so the plugin will now disable.");

            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        getFileManager().addFile(new File(getDataFolder(), "config.yml"))
                .addFile(new File(getDataFolder(), "data.yml"))
                .addFile(new File(getDataFolder(), "messages.yml"))
                .init();

        InventoryManager.loadButtons();

        this.crazyManager = new CrazyManager();

        this.crazyManager.load();

        final PluginManager manager = getServer().getPluginManager();

        manager.registerEvents(new AuctionsMenu(), this); // register main menu
        manager.registerEvents(new CategoriesMenu(), this); // register categories menu
        manager.registerEvents(new CurrentMenu(), this); // register current listings menu
        manager.registerEvents(new ExpiredMenu(), this); // register expired menu
        manager.registerEvents(new BuyingMenu(), this); // register buying menu
        manager.registerEvents(new BidMenu(), this); // register bid menu

        manager.registerEvents(new MiscListener(), this);
        manager.registerEvents(new MarcoListener(), this);

        registerCommand(getCommand("crazyauctions"), new AuctionTab(), new AuctionCommand());

        for (final Permissions permission : Permissions.values()) {
            if (permission.shouldRegister()) {
                permission.registerPermission();
            }
        }

        this.support = new VaultSupport();
        this.support.loadVault();

        new FoliaRunnable(getServer().getGlobalRegionScheduler()) {
            @Override
            public void run() {
                Methods.updateAuction();
            }
        }.runAtFixedRate(this, 0L, 5000L);

        Messages.addMissingMessages();

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

    public final CrazyManager getCrazyManager() {
        return this.crazyManager;
    }
}