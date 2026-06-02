package com.badbones69.crazyauctions.commands;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.CrazyPlatform;
import com.badbones69.crazyauctions.api.enums.*;
import com.badbones69.crazyenvoys.enums.Files;
import com.badbones69.crazyauctions.api.events.AuctionCancelledEvent;
import com.badbones69.crazyauctions.api.events.AuctionListEvent;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.enums.ShopType;
import us.crazycrew.api.storage.IStorageHolder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionCommand implements CommandExecutor {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final Server server = this.plugin.getServer();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final IStorageHolder holder = this.platform.getStorageHolder();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        final FileConfiguration config = Files.config.getConfiguration();

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage(sender));

                return true;
            }

            if (!Methods.hasPermission(sender, "access")) {
                return true;
            }

            if (config.contains("Settings.Category-Page-Opens-First")) {
                if (config.getBoolean("Settings.Category-Page-Opens-First")) {
                    GuiListener.openCategories(player, ShopType.SELL);

                    return true;
                }
            }

            if (this.platform.isSellModuleEnabled()) {
                GuiListener.openShop(player, ShopType.SELL, Category.NONE, 1);
            } else if (this.platform.isBidModuleEnabled()) {
                GuiListener.openShop(player, ShopType.BID, Category.NONE, 1);
            } else {
                player.sendMessage(Methods.getPrefix() + Methods.color("&cThe bidding and selling options are both disabled. Please contact the admin about this."));
            }

            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help" -> {
                if (!Methods.hasPermission(sender, "access")) {
                    return true;
                }

                sender.sendMessage(Messages.HELP.getMessage(sender));

                return true;
            }

            case "reload" -> {
                if (!Methods.hasPermission(sender, "reload")) {
                    return true;
                }

                this.platform.reload();

                sender.sendMessage(Messages.RELOAD.getMessage(sender));

                return true;
            }

            case "force_end_all" -> {
                if (!Methods.hasPermission(sender, "force-end-all")) {
                    return true;
                }

                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage(sender));

                    return true;
                }

                end(player);

                return true;
            }

            case "view" -> {
                if (!Methods.hasPermission(sender, "view")) {
                    return true;
                }

                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage(sender));

                    return true;
                }

                if (args.length >= 2) {
                    GuiListener.openViewer(player, args[1], 1);

                    return true;
                }

                sender.sendMessage(Messages.CRAZYAUCTIONS_VIEW.getMessage(sender));

                return true;
            }

            case "expired", "collect" -> {
                if (!Methods.hasPermission(sender, "access")) {
                    return true;
                }

                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage(sender));

                    return true;
                }

                GuiListener.openPlayersExpiredList(player, 1);

                return true;
            }

            case "listed" -> {
                if (!Methods.hasPermission(sender, "access")) return true;

                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage(sender));

                    return true;
                }

                GuiListener.openPlayersCurrentList(player, 1);

                return true;
            }

            case "sell", "bid" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage(sender));

                    return true;
                }

                if (args.length >= 2) {
                    if (args[0].equalsIgnoreCase("sell")) {
                        if (!this.platform.isSellModuleEnabled()) {
                            player.sendMessage(Messages.SELLING_DISABLED.getMessage(sender));

                            return true;
                        }

                        if (!Methods.hasPermission(player, "sell")) return true;
                    }

                    if (args[0].equalsIgnoreCase("bid")) {
                        if (!this.platform.isBidModuleEnabled()) {
                            player.sendMessage(Messages.BIDDING_DISABLED.getMessage(sender));

                            return true;
                        }

                        if (!Methods.hasPermission(player, "bid")) return true;
                    }

                    final ItemStack item = Methods.getItemInHand(player);

                    int amount = item.getAmount();

                    if (args.length >= 3) {
                        if (!Methods.isInt(args[2])) {
                            Map<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Arg%", args[2]);
                            placeholders.put("%arg%", args[2]);

                            player.sendMessage(Messages.NOT_A_NUMBER.getMessage(sender, placeholders));

                            return true;
                        }

                        amount = Integer.parseInt(args[2]);

                        if (amount <= 0) amount = 1;

                        if (amount > item.getAmount()) amount = item.getAmount();
                    }

                    String stringPrice = args[1].toLowerCase()
                            .replaceAll("tn", "000000000000")
                            .replaceAll("bn", "000000000")
                            .replaceAll("m", "000000")
                            .replaceAll("k", "000");

                    if (!Methods.isLong(stringPrice)) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Arg%", stringPrice);
                        placeholders.put("%arg%", stringPrice);

                        player.sendMessage(Messages.NOT_A_NUMBER.getMessage(sender, placeholders));

                        return true;
                    }

                    if (Methods.getItemInHand(player).getType() == Material.AIR) {
                        player.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage(sender));

                        return false;
                    }

                    long price = Long.parseLong(stringPrice);

                    if (args[0].equalsIgnoreCase("bid")) {
                        if (price < config.getLong("Settings.Minimum-Bid-Price", 100)) {
                            player.sendMessage(Messages.BID_PRICE_TO_LOW.getMessage(sender));

                            return true;
                        }

                        if (price > config.getLong("Settings.Max-Beginning-Bid-Price", 1000000)) {
                            player.sendMessage(Messages.BID_PRICE_TO_HIGH.getMessage(sender));

                            return true;
                        }
                    } else {
                        if (price < config.getLong("Settings.Minimum-Sell-Price", 10)) {
                            player.sendMessage(Messages.SELL_PRICE_TO_LOW.getMessage(sender));

                            return true;
                        }
                        if (price > config.getLong("Settings.Max-Beginning-Sell-Price", 1000000)) {
                            player.sendMessage(Messages.SELL_PRICE_TO_HIGH.getMessage(sender));

                            return true;
                        }
                    }

                    if (!player.hasPermission("crazyauctions.bypass")) {
                        int SellLimit = 0;
                        int BidLimit = 0;

                        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
                            String perm = permission.getPermission();

                            if (perm.startsWith("crazyauctions.sell.")) {
                                perm = perm.replace("crazyauctions.sell.", "");

                                if (Methods.isInt(perm)) {
                                    if (Integer.parseInt(perm) > SellLimit) {
                                        SellLimit = Integer.parseInt(perm);
                                    }
                                }
                            }

                            if (perm.startsWith("crazyauctions.bid.")) {
                                perm = perm.replace("crazyauctions.bid.", "");

                                if (Methods.isInt(perm)) {
                                    if (Integer.parseInt(perm) > BidLimit) {
                                        BidLimit = Integer.parseInt(perm);
                                    }
                                }
                            }
                        }

                        for (int i = 1; i < 100; i++) {
                            if (SellLimit < i) {
                                if (player.hasPermission("crazyauctions.sell." + i)) {
                                    SellLimit = i;
                                }
                            }

                            if (BidLimit < i) {
                                if (player.hasPermission("crazyauctions.bid." + i)) {
                                    BidLimit = i;
                                }
                            }
                        }

                        if (args[0].equalsIgnoreCase("sell")) {
                            final List<ItemStack> items = this.platform.getItems(player);

                            if (items.size() >= SellLimit) {
                                player.sendMessage(Messages.MAX_ITEMS.getMessage(sender));

                                return true;
                            }
                        }

                        if (args[0].equalsIgnoreCase("bid")) {
                            final List<ItemStack> items = this.platform.getItems(player);

                            if (items.size() >= BidLimit) {
                                player.sendMessage(Messages.MAX_ITEMS.getMessage(sender));

                                return true;
                            }
                        }
                    }

                    if (config.getStringList("Settings.BlackList").contains(item.getType().getKey().getKey())) {
                        player.sendMessage(Messages.ITEM_BLACKLISTED.getMessage(sender));

                        return true;
                    }

                    List<String> pdcBlacklist = config.getStringList("Settings.PDC-BlackList");

                    if (item.getPersistentDataContainer().getKeys().stream().anyMatch(key -> pdcBlacklist.contains(key.asString()))) {
                        player.sendMessage(Messages.ITEM_BLACKLISTED.getMessage(sender));

                        return true;
                    }

                    if (!config.getBoolean("Settings.Allow-Damaged-Items", false)) {
                        if (item instanceof Damageable damageable) {
                            if (damageable.getDamage() > 0) {
                                player.sendMessage(Messages.ITEM_DAMAGED.getMessage(sender));

                                return true;
                            }
                        }
                    }

                    final VaultSupport vaultSupport = plugin.getSupport();

                    int listCost = config.getInt("Settings.Auction-List-Fee", 0);

                    if (vaultSupport.getMoney(player) >= listCost) {
                        vaultSupport.removeMoney(player, listCost);
                    } else {
                        Map<String, String> placeholders = new HashMap<>();

                        placeholders.put("%Money_Needed%", String.valueOf(listCost));
                        placeholders.put("%money_needed%", String.valueOf(listCost));

                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(sender, placeholders));

                        return true;
                    }

                    final ShopType type = args[0].equalsIgnoreCase("bid") ? ShopType.BID : ShopType.SELL;

                    final ItemStack stack = item.clone();

                    stack.setAmount(amount);

                    this.holder.addItem(
                            player.getUniqueId(),
                            player.getName(),
                            Methods.toBase64(stack),
                            price,
                            args[0].equalsIgnoreCase("bid") ? ShopType.BID : ShopType.SELL
                    );

                    new AuctionListEvent(player, type, stack, price).callEvent();

                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Price%", String.valueOf(price));
                    placeholders.put("%price%", String.valueOf(price));

                    player.sendMessage(Messages.ADDED_ITEM_TO_AUCTION.getMessage(sender, placeholders));

                    if (item.getAmount() <= 1 || (item.getAmount() - amount) <= 0) {
                        Methods.setItemInHand(player, new ItemStack(Material.AIR));
                    } else {
                        item.setAmount(item.getAmount() - amount);
                    }

                    return false;
                }

                sender.sendMessage(Messages.CRAZYAUCTIONS_SELL_BID.getMessage(sender));
            }

            default -> {
                sender.sendMessage(Methods.getPrefix("&cPlease do /crazyauctions help for more information."));

                return true;
            }
        }

        return true;
    }

    /**
     * Force ends all current listed items from the auction house.
     *
     * @param player The {@link Player} that initiated the cancellation.
     * @see AuctionCancelledEvent
     */
    private void end(@NonNull final Player player) {
        final YamlConfiguration data = Files.data.getConfiguration();

        int number = 1;

        final ConfigurationSection section = data.getConfigurationSection("Items");

        if (section == null) {
            return;
        }

        for (final String id : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(id);

            if (item == null) continue;

            final String seller = item.getString("Seller", "");

            if (seller.isBlank()) continue;

            final UUID uuid = UUID.fromString(seller);

            final Player entity = this.server.getPlayer(uuid);

            if (entity != null) {
                entity.sendMessage(Messages.ADMIN_FORCE_CANCELLED_TO_PLAYER.getMessage(player));
            }

            number = Methods.expireItem(number, this.server.getOfflinePlayer(uuid), id, data, Reasons.ADMIN_FORCE_CANCEL);
        }

        Files.data.save();

        player.sendMessage(Messages.ADMIN_FORCE_CANCELLED_ALL.getMessage(player));
    }
}