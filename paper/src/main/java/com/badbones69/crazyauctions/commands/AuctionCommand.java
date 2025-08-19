package com.badbones69.crazyauctions.commands;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.enums.*;
import com.badbones69.crazyauctions.api.events.AuctionCancelledEvent;
import com.badbones69.crazyauctions.api.events.AuctionListEvent;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.ryderbelserion.vital.paper.api.files.FileManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class AuctionCommand implements CommandExecutor {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final FileManager fileManager = this.plugin.getFileManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

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

            if (crazyManager.isSellingEnabled()) {
                GuiListener.openShop(player, ShopType.SELL, Category.NONE, 1);
            } else if (crazyManager.isBiddingEnabled()) {
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

                this.fileManager.reloadFiles().init();

                this.crazyManager.load();

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

                forceEndAll(player);

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

case "search" -> {
    if (!(sender instanceof Player player)) {
        sender.sendMessage(Messages.PLAYERS_ONLY.getMessage(sender));
        return true;
    }

    if (!Methods.hasPermission(sender, "search")) return true;

    if (args.length < 2) {
        player.sendMessage(Methods.getPrefix("&cUsage: /ah search <item> [player]"));
        return true;
    }

    // Combine args[1..n-1] into itemName in case quotes are used
    String itemName = args[1];
    if (args.length > 2) {
        StringBuilder sb = new StringBuilder(itemName);
        for (int i = 2; i < args.length; i++) {
            if (!args[i].startsWith("\"") && !args[i].endsWith("\"")) {
                sb.append(" ").append(args[i]);
            } else {
                sb.append(args[i]);
            }
        }
        itemName = sb.toString().replace("\"", "");
    }

    String playerFilter = null;
    if (args.length >= 3) {
        playerFilter = args[2];
    }

    FileConfiguration data = Files.data.getConfiguration();
    List<String> results = new ArrayList<>();

    for (String key : data.getConfigurationSection("Items").getKeys(false)) {
        String sellerName = data.getString("Items." + key + ".SellerName", "");
        String itemBase64 = data.getString("Items." + key + ".Item", "");

        ItemStack item = Methods.fromBase64(itemBase64);
        if (item == null) continue;

        // Check item name
        String displayName = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ?
                item.getItemMeta().getDisplayName() : item.getType().name();

        if (displayName.toLowerCase().contains(itemName.toLowerCase())) {
            // Check optional player filter
            if (playerFilter == null || sellerName.equalsIgnoreCase(playerFilter)) {
                results.add(displayName + " | Seller: " + sellerName + " | Price: " + data.getLong("Items." + key + ".Price"));
            }
        }
    }

    if (results.isEmpty()) {
        player.sendMessage(Methods.getPrefix("&cNo matching items found."));
    } else {
        player.sendMessage(Methods.getPrefix("&aSearch results:"));
        results.forEach(line -> player.sendMessage("  " + line));
    }

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
                        if (!crazyManager.isSellingEnabled()) {
                            player.sendMessage(Messages.SELLING_DISABLED.getMessage(sender));

                            return true;
                        }

                        if (!Methods.hasPermission(player, "sell")) return true;
                    }

                    if (args[0].equalsIgnoreCase("bid")) {
                        if (!crazyManager.isBiddingEnabled()) {
                            player.sendMessage(Messages.BIDDING_DISABLED.getMessage(sender));

                            return true;
                        }

                        if (!Methods.hasPermission(player, "bid")) return true;
                    }

                    ItemStack item = Methods.getItemInHand(player);
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
                            if (crazyManager.getItems(player, ShopType.SELL).size() >= SellLimit) {
                                player.sendMessage(Messages.MAX_ITEMS.getMessage(sender));

                                return true;
                            }
                        }

                        if (args[0].equalsIgnoreCase("bid")) {
                            if (crazyManager.getItems(player, ShopType.BID).size() >= BidLimit) {
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

                    VaultSupport vaultSupport = plugin.getSupport();
                    int listCost = config.getInt("Settings.Auction-List-Fee", 0);

                    if (vaultSupport.getMoney(player) >= listCost) {
                        vaultSupport.removeMoney(player, listCost);
                    } else {
                        Map<String, String> placeholders = new HashMap<>() {{
                            put("%Money_Needed%", String.valueOf(listCost));
                            put("%money_needed%", String.valueOf(listCost));
                        }};

                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(sender, placeholders));
                        return true;
                    }

                    String seller = player.getUniqueId().toString();

                    int num = 1;

                    while (data.contains("Items." + num)) num++;

                    data.set("Items." + num + ".Price", price);
                    data.set("Items." + num + ".Seller", seller);
                    data.set("Items." + num + ".SellerName", player.getName());

                    if (args[0].equalsIgnoreCase("bid")) {
                        data.set("Items." + num + ".Time-Till-Expire", Methods.convertToMill(config.getString("Settings.Bid-Time", "2m 30s")));
                    } else {
                        data.set("Items." + num + ".Time-Till-Expire", Methods.convertToMill(config.getString("Settings.Sell-Time", "2d")));
                    }

                    data.set("Items." + num + ".Full-Time", Methods.convertToMill(config.getString("Settings.Full-Expire-Time", "10d")));
                    int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);

                    // Runs 3x to check for same ID.
                    for (String i : data.getConfigurationSection("Items").getKeys(false))
                        if (data.getInt("Items." + i + ".StoreID") == id)
                            id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
                    for (String i : data.getConfigurationSection("Items").getKeys(false))
                        if (data.getInt("Items." + i + ".StoreID") == id)
                            id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
                    for (String i : data.getConfigurationSection("Items").getKeys(false))
                        if (data.getInt("Items." + i + ".StoreID") == id)
                            id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);

                    data.set("Items." + num + ".StoreID", id);
                    ShopType type = ShopType.SELL;

                    if (args[0].equalsIgnoreCase("bid")) {
                        data.set("Items." + num + ".Biddable", true);
                        type = ShopType.BID;
                    } else {
                        data.set("Items." + num + ".Biddable", false);
                    }

                    data.set("Items." + num + ".TopBidder", "None");
                    data.set("Items." + num + ".TopBidderName", "None");

                    ItemStack stack = item.clone();
                    stack.setAmount(amount);

                    data.set("Items." + num + ".Item", Methods.toBase64(stack));

                    Files.data.save();

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
    private void forceEndAll(Player player) {
        FileConfiguration data = Files.data.getConfiguration();

        int num = 1;

        for (String i : data.getConfigurationSection("Items").getKeys(false)) {

            OfflinePlayer seller = Methods.getOfflinePlayer(data.getString("Items." + i + ".Seller"));

            if (seller.getPlayer() != null) {
                seller.getPlayer().sendMessage(Messages.ADMIN_FORCE_CANCELLED_TO_PLAYER.getMessage(player));
            }

            num = Methods.expireItem(num, seller, i, data, Reasons.ADMIN_FORCE_CANCEL);

        }

        Files.data.save();

        player.sendMessage(Messages.ADMIN_FORCE_CANCELLED_ALL.getMessage(player));

    }

}
