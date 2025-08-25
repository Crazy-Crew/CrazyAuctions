package com.badbones69.crazyauctions.commands.features.admin.migrate.interfaces.types;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.commands.features.admin.migrate.enums.MigrationType;
import com.badbones69.crazyauctions.commands.features.admin.migrate.interfaces.IAuctionMigrator;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AuctionsDeprecated extends IAuctionMigrator {

    public AuctionsDeprecated(@NotNull final CommandSender sender, @NotNull final MigrationType type) {
        super(sender, type);
    }

    @Override
    public void run() {
        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        final List<String> auctions = new ArrayList<>();

        switch (this.type) {
            case AUCTIONS_CONFIG -> {
                try {
                    final FileConfiguration config = Files.config.getConfiguration();

                    if (config.contains("Settings.GUISettings.OtherSettings.Refesh")) {
                        config.set("Settings.GUISettings.OtherSettings.Refresh", config.get("Settings.GUISettings.OtherSettings.Refesh", true));

                        Files.config.save();
                    }

                    success.add("&a⤷ config.yml");
                } catch (final Exception exception) {
                    failed.add("&c⤷ config.yml");
                }
            }

            case AUCTIONS_DATA -> {
                try {
                    final FileConfiguration data = Files.data.getConfiguration();

                    if (data.contains("OutOfTime/Cancelled")) {
                        final ConfigurationSection section = data.getConfigurationSection("OutOfTime/Cancelled");

                        if (section != null) {
                            for (final String path : section.getKeys(false)) {
                                final ConfigurationSection auction = section.getConfigurationSection(path);

                                if (auction == null) {
                                    auctions.add("&c%s".formatted(path));

                                    continue;
                                }

                                final ItemStack itemStack = auction.getItemStack("Item");

                                boolean shouldSave = false;

                                if (itemStack != null) {
                                    section.set("Item", Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));

                                    shouldSave = true;
                                } else {
                                    auctions.add("&c%s".formatted(path));
                                }

                                final String seller = section.getString("Seller");

                                if (seller != null) {
                                    section.set("Seller", Methods.getOfflinePlayer(seller).getUniqueId().toString());

                                    shouldSave = true;
                                } else {
                                    auctions.add("&c%s".formatted(path));
                                }

                                if (shouldSave) {
                                    Files.data.save();
                                }
                            }
                        }
                    }

                    if (data.contains("Items")) {
                        final ConfigurationSection section = data.getConfigurationSection("Items");

                        if (section != null) {
                            for (final String path : section.getKeys(false)) {
                                final ConfigurationSection auction = section.getConfigurationSection(path);

                                if (auction == null) {
                                    auctions.add("&c%s".formatted(path));

                                    continue;
                                }

                                final ItemStack itemStack = auction.getItemStack("Item");

                                boolean shouldSave = false;

                                if (itemStack != null) {
                                    section.set("Item", Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));

                                    shouldSave = true;
                                } else {
                                    auctions.add("&c%s".formatted(path));
                                }

                                final String seller = section.getString("Seller");

                                if (seller != null) {
                                    final String uuid = Methods.getOfflinePlayer(seller).getUniqueId().toString();

                                    if (!seller.equals(uuid)) {
                                        section.set("Seller", uuid);

                                        shouldSave = true;
                                    }
                                } else {
                                    auctions.add("&c%s".formatted(path));
                                }

                                final String bidder = section.getString("TopBidder");

                                if (bidder != null && !bidder.equals("None")) {
                                    final String uuid = Methods.getOfflinePlayer(bidder).getUniqueId().toString();

                                    if (!bidder.equals(uuid)) {
                                        section.set("TopBidder", uuid);

                                        shouldSave = true;
                                    }
                                }

                                if (shouldSave) {
                                    Files.data.save();
                                }
                            }
                        }
                    }
                } catch (final Exception exception) {
                    failed.add("&c⤷ data.yml");

                    this.fusion.log("error", "<red>Failed to migrate data.yml", exception);
                }
            }
        }

        final int convertedCount = success.size();
        final int failedCount = failed.size();

        sendMessage(new ArrayList<>(failedCount + convertedCount) {{
            addAll(failed);
            addAll(success);
        }}, auctions, convertedCount, failedCount);
    }
}