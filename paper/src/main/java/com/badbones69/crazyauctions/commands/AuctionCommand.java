package com.badbones69.crazyauctions.commands;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.events.AuctionListEvent;
import com.badbones69.crazyauctions.api.GuiManager;
import com.badbones69.crazyauctions.tasks.InventoryManager;
import com.badbones69.crazyauctions.tasks.UserManager;
import com.ryderbelserion.vital.paper.api.files.FileManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuctionCommand implements CommandExecutor {

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final UserManager userManager = this.plugin.getUserManager();

    private final FileManager fileManager = this.plugin.getFileManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        FileConfiguration config = Files.config.getConfiguration();

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
                    GuiManager.openCategories(player, ShopType.SELL);

                    return true;
                }
            }

            if (this.crazyManager.isSellingEnabled()) {
                GuiManager.openShop(player, ShopType.SELL, Category.NONE, 1);
            } else if (this.crazyManager.isBiddingEnabled()) {
                GuiManager.openShop(player, ShopType.BID, Category.NONE, 1);
            } else {
                player.sendMessage(Methods.getPrefix() + Methods.color("&cThe bidding and selling options are both disabled. Please contact the admin about this."));
            }

            return true;
        } else {
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

                    Files.config.reload();
                    Files.data.reload();
                    Files.messages.reload();

                    this.fileManager.reloadFiles().init();

                    // update it again!
                    this.userManager.updateAuctionsCache();

                    // we want to update this cache, after the cache above... because we will also calculate if items are expired!
                    this.userManager.updateExpiredCache();

                    //todo() close inventories by tracking viewers, so the cache can be updated than re-open their inventories
                    //todo() we need to track the specific inventory they opened, and if it's for them or another player

                    this.crazyManager.load();

                    InventoryManager.loadButtons();

                    sender.sendMessage(Messages.RELOAD.getMessage(sender));

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
                        GuiManager.openViewer(player, args[1], 1);

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

                    GuiManager.openPlayersExpiredList(player, 1);

                    return true;
                }

                case "listed" -> {
                    if (!Methods.hasPermission(sender, "access")) return true;

                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Messages.PLAYERS_ONLY.getMessage(sender));

                        return true;
                    }

                    GuiManager.openPlayersCurrentList(player, 1);

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

                        if (!Methods.isLong(args[1])) {
                            Map<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Arg%", args[1]);
                            placeholders.put("%arg%", args[1]);

                            player.sendMessage(Messages.NOT_A_NUMBER.getMessage(sender, placeholders));

                            return true;
                        }

                        if (Methods.getItemInHand(player).getType() == Material.AIR) {
                            player.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage(sender));

                            return false;
                        }

                        long price = Long.parseLong(args[1]);

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

                        if (config.getStringList("Settings.BlackList").contains(item.getType().getKey().getKey().toUpperCase())) {
                            player.sendMessage(Messages.ITEM_BLACKLISTED.getMessage(sender));

                            return true;
                        }

                        if (!config.getBoolean("Settings.Allow-Damaged-Items", false)) {
                            for (Material i : getDamageableItems()) {
                                if (item.getType() == i) {
                                    if (item instanceof Damageable damageable) {
                                        if (damageable.getDamage() > 0) {
                                            player.sendMessage(Messages.ITEM_DAMAGED.getMessage(sender));

                                            return true;
                                        }
                                    }
                                }
                            }
                        }

                        /*if (!allowBook(item)) {
                            player.sendMessage(Messages.BOOK_NOT_ALLOWED.getMessage(sender));

                            return true;
                        }*/

                        ShopType type = args[0].equalsIgnoreCase("bid") ? ShopType.BID : ShopType.SELL;

                        ItemStack stack = item.clone();
                        stack.setAmount(amount);

                        this.userManager.addAuction(player, stack, price, args[0].equalsIgnoreCase("bid"));

                        this.plugin.getServer().getPluginManager().callEvent(new AuctionListEvent(player, type, stack, price));

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
                    sender.sendMessage(Messages.HELP_MSG.getMessage(sender));

                    return true;
                }
            }

            return true;
        }
    }

    private ArrayList<Material> getDamageableItems() {
        ArrayList<Material> ma = new ArrayList<>();

        ma.add(Material.GOLDEN_HELMET);
        ma.add(Material.GOLDEN_CHESTPLATE);
        ma.add(Material.GOLDEN_LEGGINGS);
        ma.add(Material.GOLDEN_BOOTS);
        ma.add(Material.GOLDEN_HOE);
        ma.add(Material.WOODEN_SWORD);
        ma.add(Material.WOODEN_PICKAXE);
        ma.add(Material.WOODEN_AXE);
        ma.add(Material.WOODEN_SHOVEL);
        ma.add(Material.WOODEN_HOE);
        ma.add(Material.STONE_SHOVEL);
        ma.add(Material.IRON_SHOVEL);
        ma.add(Material.DIAMOND_SHOVEL);
        ma.add(Material.CROSSBOW);
        ma.add(Material.TRIDENT);
        ma.add(Material.TURTLE_HELMET);
        ma.add(Material.DIAMOND_HELMET);
        ma.add(Material.DIAMOND_CHESTPLATE);
        ma.add(Material.DIAMOND_LEGGINGS);
        ma.add(Material.DIAMOND_BOOTS);
        ma.add(Material.CHAINMAIL_HELMET);
        ma.add(Material.CHAINMAIL_CHESTPLATE);
        ma.add(Material.CHAINMAIL_LEGGINGS);
        ma.add(Material.CHAINMAIL_BOOTS);
        ma.add(Material.IRON_HELMET);
        ma.add(Material.IRON_CHESTPLATE);
        ma.add(Material.IRON_LEGGINGS);
        ma.add(Material.IRON_BOOTS);
        ma.add(Material.LEATHER_HELMET);
        ma.add(Material.LEATHER_CHESTPLATE);
        ma.add(Material.LEATHER_LEGGINGS);
        ma.add(Material.LEATHER_BOOTS);
        ma.add(Material.BOW);
        ma.add(Material.STONE_SWORD);
        ma.add(Material.IRON_SWORD);
        ma.add(Material.DIAMOND_SWORD);
        ma.add(Material.STONE_AXE);
        ma.add(Material.IRON_AXE);
        ma.add(Material.DIAMOND_AXE);
        ma.add(Material.STONE_PICKAXE);
        ma.add(Material.IRON_PICKAXE);
        ma.add(Material.DIAMOND_PICKAXE);
        ma.add(Material.STONE_AXE);
        ma.add(Material.IRON_AXE);
        ma.add(Material.DIAMOND_AXE);
        ma.add(Material.STONE_HOE);
        ma.add(Material.IRON_HOE);
        ma.add(Material.DIAMOND_HOE);
        ma.add(Material.FLINT_AND_STEEL);
        ma.add(Material.ANVIL);
        ma.add(Material.FISHING_ROD);

        return ma;
    }

    /*private boolean allowBook(ItemStack item) {
        if (item != null && item.hasItemMeta() && item.getItemMeta() instanceof BookMeta bookMeta) {
            this.plugin.getLogger().info("Checking " + item.getType() + " for illegal unicode.");

            FileConfiguration file = Files.test_file.getConfiguration();

            try {
                file.set("Test", item);

                Files.test_file.save();

                this.plugin.getLogger().info(item.getType() + " has passed unicode checks.");
            } catch (YAMLException exception) {
                this.plugin.getLogger().log(Level.SEVERE, item.getType() + " has failed unicode checks and has been denied.", exception);

                return false;
            }

            return bookMeta.getPages().stream().mapToInt(String :: length).sum() < 2000;
        }

        return true;
    }*/
}