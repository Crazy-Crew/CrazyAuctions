package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.support.MetricsWrapper;
import com.badbones69.crazyauctions.commands.features.CommandHandler;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.controllers.MarcoListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.badbones69.crazyauctions.datafixer.ConfigFixer;
import com.ryderbelserion.fusion.core.api.enums.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.api.enums.Scheduler;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import com.ryderbelserion.fusion.paper.files.FileManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.Base64;

public class CrazyAuctions extends JavaPlugin {

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private CrazyManager crazyManager;

    private VaultSupport support;

    private FusionPaper fusion;
    private FileManager fileManager;

    @Override
    public void onEnable() {
        final Server server = getServer();
        final PluginManager pluginManager = server.getPluginManager();

        if (!pluginManager.isPluginEnabled("Vault")) {
            getLogger().severe("Vault was not found so the plugin will now disable.");

            pluginManager.disablePlugin(this);

            return;
        }

        this.fusion = new FusionPaper(this);

        this.fileManager = this.fusion.getFileManager();

        final Path path = getDataPath();

        this.fileManager.addFile(path.resolve("config.yml"), FileType.PAPER)
                .addFile(path.resolve("data.yml"), FileType.PAPER)
                .addFile(path.resolve("messages.yml"), FileType.PAPER);

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
                    OfflinePlayer player = Methods.getOfflinePlayer(uuid);

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
                    OfflinePlayer player = Methods.getOfflinePlayer(uuid);

                    if (!uuid.equals(player.getUniqueId().toString())) {
                        configuration.set("Items." + key + ".Seller", player.getUniqueId().toString());

                        Files.data.save();
                    }
                }

                final String bidder = configuration.getString("Items." + key + ".TopBidder");

                if (bidder != null && !bidder.equals("None")) {
                    OfflinePlayer player = Methods.getOfflinePlayer(bidder);

                    if (!bidder.equals(player.getUniqueId().toString())) {
                        configuration.set("Items." + key + ".TopBidder", player.getUniqueId().toString());

                        Files.data.save();
                    }
                }
            }
        }

        this.crazyManager.load();

        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(new MarcoListener(), this);

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

    public final FileManager getFileManager() {
        return this.fileManager;
    }

    public final FusionPaper getFusion() {
        return this.fusion;
    }
}