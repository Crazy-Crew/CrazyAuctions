package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.commands.AuctionCommand;
import com.badbones69.crazyauctions.commands.AuctionTab;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.controllers.MarcoListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.badbones69.crazyauctions.datafixer.ConfigFixer;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import com.ryderbelserion.fusion.paper.builders.folia.Scheduler;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

public class CrazyAuctions extends JavaPlugin {

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private CrazyManager crazyManager;

    private VaultSupport support;

    private FusionPaper fusion;

    @Override
    public void onEnable() {
        this.fusion = new FusionPaper(this);
        this.fusion.init();

        if (this.fusion.isPluginEnabled("Vault")) {
            this.fusion.log(Level.ERROR, "Vault was not found on the server, so the plugin will not function!");

            return;
        }

        final PaperFileManager fileManager = this.fusion.getFileManager();

        final Path path = getDataPath();

        fileManager.addPaperFile(path.resolve("config.yml"))
                .addPaperFile(path.resolve("data.yml"))
                .addPaperFile(path.resolve("messages.yml"));

        this.crazyManager = new CrazyManager();

        YamlConfiguration configuration = Files.data.getConfiguration();

        final ConfigurationSection cancelled = configuration.getConfigurationSection("OutOfTime/Cancelled");

        if (cancelled != null) {
            for (String key : cancelled.getKeys(false)) {
                final ItemStack itemStack = configuration.getItemStack("OutOfTime/Cancelled." + key + ".Item");

                if (itemStack != null) {
                    configuration.set("OutOfTime/Cancelled." + key + ".Item", Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));

                    Files.data.save();
                }

                final String uuid = configuration.getString("OutOfTime/Cancelled." + key + ".Seller");

                if (uuid != null) {
                    OfflinePlayer player = Methods.getOfflinePlayer(uuid);

                    configuration.set("OutOfTime/Cancelled." + key + ".Seller", player.getUniqueId().toString());

                    Files.data.save();
                }
            }
        }

        final ConfigurationSection items = configuration.getConfigurationSection("Items");

        if (items != null) {
            for (String key : items.getKeys(false)) {
                final ConfigurationSection section = items.getConfigurationSection(key);

                if (section == null) continue;

                final String item = section.getString("Item", "");

                if (item.isBlank()) continue;

                final ItemStack itemStack = configuration.getItemStack("Item", ItemType.AIR.createItemStack());

                boolean isSaving = false;

                if (!itemStack.isEmpty()) {
                    section.set("Item", ItemUtils.toBase64(itemStack));

                    isSaving = true;
                }

                final String seller = section.getString("Seller", "");

                if (!seller.isBlank()) {
                    final OfflinePlayer player = Methods.getOfflinePlayer(seller);
                    final String id = player.getUniqueId().toString();

                    if (!seller.equals(id)) {
                        section.set("Seller", id);

                        isSaving = true;
                    }
                }

                final String bidder = section.getString("TopBidder", "None");

                if (!bidder.isBlank() && !bidder.equalsIgnoreCase("None")) {
                    final OfflinePlayer player = Methods.getOfflinePlayer(bidder);
                    final String id = player.getUniqueId().toString();

                    if (!bidder.equals(id)) {
                        section.set("TopBidder", id);

                        isSaving = true;
                    }
                }

                if (isSaving) {
                    Files.data.save();
                }
            }
        }

        this.crazyManager.load();

        final Server server = getServer();
        final PluginManager pluginManager = server.getPluginManager();

        List.of(
                new GuiListener(),
                new MarcoListener()
        ).forEach(listener -> pluginManager.registerEvents(listener, this));

        registerCommand(getCommand("crazyauctions"), new AuctionTab(), new AuctionCommand());

        this.support = new VaultSupport();
        this.support.setupEconomy();

        new FoliaScheduler(this, Scheduler.global_scheduler) {
            @Override
            public void run() {
                Methods.updateAuction();
            }
        }.runAtFixedRate(0L, 5000L);

        Messages.addMissingMessages();

        new ConfigFixer().onEnable();

        new Metrics(this, 4624);
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

    public final PaperFileManager getFileManager() {
        return this.fusion.getFileManager();
    }

    public final FusionPaper getFusion() {
        return this.fusion;
    }
}