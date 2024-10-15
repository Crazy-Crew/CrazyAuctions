package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.UserManager;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.listeners.CacheListener;
import com.badbones69.crazyauctions.api.support.MetricsWrapper;
import com.badbones69.crazyauctions.commands.AuctionCommand;
import com.badbones69.crazyauctions.commands.AuctionTab;
import com.badbones69.crazyauctions.commands.v2.BaseCommand;
import com.badbones69.crazyauctions.commands.v2.player.CommandHelp;
import com.badbones69.crazyauctions.commands.v2.staff.CommandReload;
import com.badbones69.crazyauctions.configs.ConfigManager;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.controllers.MarcoListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ryderbelserion.vital.paper.Vital;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.Base64;
import java.util.List;

public class CrazyAuctions extends Vital {

    public @NotNull static CrazyAuctions getPlugin() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private CrazyManager crazyManager;
    private UserManager userManager;

    private VaultSupport support;

    @Override
    public void onEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            getLogger().severe("Vault was not found so the plugin will now disable.");

            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        ConfigManager.load(getDataFolder());

        this.userManager = new UserManager(this);

        getFileManager().addFile(new File(getDataFolder(), "config.yml"))
                .addFile(new File(getDataFolder(), "data.yml"))
                .addFile(new File(getDataFolder(), "messages.yml"))
                .init();

        this.crazyManager = new CrazyManager();

        FileConfiguration configuration = Files.data.getConfiguration();

        if (configuration.contains("OutOfTime/Cancelled")) {
            for (String key : configuration.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                final ItemStack itemStack = configuration.getItemStack("OutOfTime/Cancelled." + key + ".Item");

                if (itemStack != null) {
                    configuration.set("OutOfTime/Cancelled." + key + ".Item", Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));

                    Files.data.save();
                }

                final String uuid = configuration.getString("OutOfTime/Cancelled." + key + ".Seller");

                if (uuid != null) {
                    OfflinePlayer player = Methods.getOfflinePlayer(uuid, false);

                    configuration.set("OutOfTime/Cancelled." + key + ".Seller", player.getUniqueId().toString());

                    Files.data.save();
                }
            }
        }

        if (configuration.contains("Items")) {
            for (String key : configuration.getConfigurationSection("Items").getKeys(false)) {
                final ItemStack itemStack = configuration.getItemStack("Items." + key + ".Item");

                if (itemStack != null) {
                    configuration.set("Items." + key + ".Item", Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));

                    Files.data.save();
                }

                final String uuid = configuration.getString("Items." + key + ".Seller");

                if (uuid != null) {
                    OfflinePlayer player = Methods.getOfflinePlayer(uuid, false);

                    if (!uuid.equals(player.getUniqueId().toString())) {
                        configuration.set("Items." + key + ".Seller", player.getUniqueId().toString());

                        Files.data.save();
                    }
                }

                final String bidder = configuration.getString("Items." + key + ".TopBidder");

                if (bidder != null && !bidder.equals("None")) {
                    OfflinePlayer player = Methods.getOfflinePlayer(bidder, false);

                    if (!bidder.equals(player.getUniqueId().toString())) {
                        configuration.set("Items." + key + ".TopBidder", player.getUniqueId().toString());

                        Files.data.save();
                    }
                }
            }
        }

        this.crazyManager.load();

        getServer().getPluginManager().registerEvents(new CacheListener(), this);
        getServer().getPluginManager().registerEvents(new MarcoListener(), this);
        getServer().getPluginManager().registerEvents(new GuiListener(), this);

        registerCommand(getCommand("ca"), new AuctionTab(), new AuctionCommand());

        // Register commands.
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            LiteralArgumentBuilder<CommandSourceStack> root = new BaseCommand().registerPermission().literal().createBuilder();

            List.of(
                    new CommandReload(),
                    new CommandHelp()
            ).forEach(command -> root.then(command.registerPermission().literal()));

            event.registrar().register(root.build(), "the base command for CrazyAuctions");
        });

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

    public final UserManager getUserManager() {
        return this.userManager;
    }
}