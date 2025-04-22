package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.*;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.Files;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.Reasons;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.events.AuctionBuyEvent;
import com.badbones69.crazyauctions.api.events.AuctionCancelledEvent;
import com.badbones69.crazyauctions.api.events.AuctionNewBidEvent;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Registry;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;

public class GuiListener implements Listener {

    private static final CrazyAuctions plugin = CrazyAuctions.get();
    private static final CrazyManager crazyManager = plugin.getCrazyManager();

    private static final Map<UUID, Integer> bidding = new HashMap<>();
    private static final Map<UUID, String> biddingID = new HashMap<>();
    private static final Map<UUID, ShopType> shopType = new HashMap<>(); // Shop Type
    private static final Map<UUID, Category> shopCategory = new HashMap<>(); // Category Type
    private static final Map<UUID, List<Integer>> List = new HashMap<>();
    private static final Map<UUID, String> IDs = new HashMap<>();

    public static void openShop(@NotNull Player player, @NotNull ShopType sell, @NotNull Category cat, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();
        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();

        if (!data.contains("Items")) {
            data.set("Items.Clear", null);

            Files.data.save();
        }

        shopCategory.put(player.getUniqueId(), cat);

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + i + ".Item"));

                List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

                if (data.contains("Items." + i + ".Item") && (cat.getItems().contains(itemBuilder.getItemStack().getType()) || cat == Category.NONE)) {
                    if (data.getBoolean("Items." + i + ".Biddable")) {
                        if (sell == ShopType.BID) {
                            String sellerName = data.getString("Items." + i + ".SellerName");

                            String price = Methods.getPrice(i, false);
                            String time = Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"));

                            String topBidderName = data.getString("Items." + i + ".TopBidderName");

                            for (String key : config.getStringList("Settings.GUISettings.Bidding")) {
                                String line = key.replace("%TopBid%", price).replace("%topbid%", price);

                                line = sellerName != null ? line.replace("%Seller%", sellerName).replace("%seller%", sellerName) : line.replace("%Seller%", "N/A").replace("%seller%", "N/A");

                                line = topBidderName != null ? line.replace("%TopBidder%", topBidderName).replace("%topbidder%", topBidderName) : line.replace("%TopBidder%", "N/A").replace("%topbidder%", "N/A");

                                lore.add(line.replace("%Time%", time).replace("%time%", time));
                            }

                            itemBuilder.setLore(lore);

                            items.add(itemBuilder.build());

                            ID.add(data.getInt("Items." + i + ".StoreID"));
                        }
                    } else {
                        if (sell == ShopType.SELL) {
                            String sellerName = data.getString("Items." + i + ".SellerName");

                            String price = Methods.getPrice(i, false);
                            String time = Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"));

                            String format = String.format(Locale.ENGLISH, "%,d", Long.parseLong(price));

                            for (String l : config.getStringList("Settings.GUISettings.SellingItemLore")) {
                                lore.add(l.replace("%Price%", format).replace("%price%", format)
                                        .replace("%Seller%", sellerName != null ? sellerName : "N/A").replace("%seller%", sellerName != null ? sellerName : "N/A")
                                        .replace("%Time%", time).replace("%time%", time));
                            }

                            itemBuilder.setLore(lore);

                            items.add(itemBuilder.build());

                            ID.add(data.getInt("Items." + i + ".StoreID"));
                        }
                    }
                }
            }
        }

        page = Math.min(Methods.getMaxPage(items), page);

        Inventory inv = new AuctionMenu(54, Methods.color(config.getString("Settings.GUIName") + " #" + page), page).getInventory();

        List<String> options = new ArrayList<>(){{
            add("SellingItems");
            add("Cancelled/ExpiredItems");
            add("PreviousPage");
            add("Refresh");
            add("NextPage");
            add("Category1");
            add("Category2");
        }};

        if (sell == ShopType.SELL) {
            shopType.put(player.getUniqueId(), ShopType.SELL);

            if (crazyManager.isBiddingEnabled()) {
                options.add("Bidding/Selling.Selling");
            }

            options.add("WhatIsThis.SellingShop");
        }

        if (sell == ShopType.BID) {
            shopType.put(player.getUniqueId(), ShopType.BID);

            if (crazyManager.isSellingEnabled()) {
                options.add("Bidding/Selling.Bidding");
            }

            options.add("WhatIsThis.BiddingShop");
        }

        for (String option : options) {
            if (config.contains("Settings.GUISettings.OtherSettings." + option + ".Toggle")) {
                if (!config.getBoolean("Settings.GUISettings.OtherSettings." + option + ".Toggle")) {
                    continue;
                }
            }

            String id = config.getString("Settings.GUISettings.OtherSettings." + option + ".Item");
            String name = config.getString("Settings.GUISettings.OtherSettings." + option + ".Name");
            int slot = config.getInt("Settings.GUISettings.OtherSettings." + option + ".Slot");
            String cName = Methods.color(config.getString("Settings.GUISettings.Category-Settings." + shopCategory.get(player.getUniqueId()).getName() + ".Name"));

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

            if (config.contains("Settings.GUISettings.OtherSettings." + option + ".Lore")) {
                for (String l : config.getStringList("Settings.GUISettings.OtherSettings." + option + ".Lore")) {
                    lore.add(l.replace("%Category%", cName).replace("%category%", cName));
                }

                inv.setItem(slot - 1, itemBuilder.setLore(lore).build());
            } else {
                inv.setItem(slot - 1, itemBuilder.setLore(lore).build());
            }
        }

        setPage(inv, page, items, ID, player);

    }

    private static void setPage(Inventory inv, int page, List<ItemStack> items, List<Integer> ID, Player player) {
        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();

            inv.setItem(slot, item);
        }
        List<Integer> Id = new ArrayList<>(Methods.getPageInts(ID, page));
        List.put(player.getUniqueId(), Id);

        player.openInventory(inv);
    }

    public static void openCategories(@NotNull Player player, @NotNull ShopType shop) {
        Methods.updateAuction();
        FileConfiguration config = Files.config.getConfiguration();

        Inventory inv = new AuctionMenu(54, Methods.color(config.getString("Settings.Categories"))).getInventory();

        List<String> options = new ArrayList<>(){{
            add("OtherSettings.Back");
            add("OtherSettings.WhatIsThis.Categories");
            add("Category-Settings.Armor");
            add("Category-Settings.Weapons");
            add("Category-Settings.Tools");
            add("Category-Settings.Food");
            add("Category-Settings.Potions");
            add("Category-Settings.Blocks");
            add("Category-Settings.Other");
            add("Category-Settings.None");
        }};

        for (String option : options) {
            if (config.contains("Settings.GUISettings." + option + ".Toggle")) {
                if (!config.getBoolean("Settings.GUISettings." + option + ".Toggle")) {
                    continue;
                }
            }

            String id = config.getString("Settings.GUISettings." + option + ".Item");
            String name = config.getString("Settings.GUISettings." + option + ".Name");
            int slot = config.getInt("Settings.GUISettings." + option + ".Slot");

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings." + option + ".Lore")) {
                itemBuilder.setLore(config.getStringList("Settings.GUISettings." + option + ".Lore"));
            }

            inv.setItem(slot - 1, itemBuilder.build());
        }

        shopType.put(player.getUniqueId(), shop);
        player.openInventory(inv);
    }

    public static void openPlayersCurrentList(@NotNull Player player, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();

        Inventory inv = new AuctionMenu(54, Methods.color(config.getString("Settings.Players-Current-Items"))).getInventory();

        List<String> options = new ArrayList<>(){{
            add("Back");
            add("WhatIsThis.CurrentItems");
        }};

        setOptions(options, config, inv);

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                if (Objects.equals(data.getString("Items." + i + ".Seller"), player.getUniqueId().toString())) {

                    String price = Methods.getPrice(i, false);
                    String time = Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"));

                    ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + i + ".Item"));

                    List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

                    for (String l : config.getStringList("Settings.GUISettings.CurrentLore")) {
                        lore.add(l.replace("%Price%", price)
                                .replace("%price%", price)
                                .replace("%Time%", time)
                                .replace("%time%", time));
                    }

                    itemBuilder.setLore(lore);

                    items.add(itemBuilder.build());

                    ID.add(data.getInt("Items." + i + ".StoreID"));
                }
            }
        }

        setPage(inv, page, items, ID, player);
    }

    public static void openPlayersExpiredList(@NotNull Player player, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();

        if (data.contains("OutOfTime/Cancelled")) {
            for (String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                if (data.getString("OutOfTime/Cancelled." + i + ".Seller") != null) {
                    if (Objects.equals(data.getString("OutOfTime/Cancelled." + i + ".Seller"), player.getUniqueId().toString())) {
                        String price = Methods.getPrice(i, true);
                        String time = Methods.convertToTime(data.getLong("OutOfTime/Cancelled." + i + ".Full-Time"));

                        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("OutOfTime/Cancelled." + i + ".Item"));

                        List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

                        for (String l : config.getStringList("Settings.GUISettings.Cancelled/ExpiredLore")) {
                            lore.add(l.replace("%Price%", price)
                                    .replace("%price%", price)
                                    .replace("%Time%", time)
                                    .replace("%time%", time));
                        }

                        itemBuilder.setLore(lore);

                        items.add(itemBuilder.build());

                        ID.add(data.getInt("OutOfTime/Cancelled." + i + ".StoreID"));
                    }
                }
            }
        }

        page = Math.min(Methods.getMaxPage(items), page);

        Inventory inv = new AuctionMenu(54, Methods.color(config.getString("Settings.Cancelled/Expired-Items") + " #" + page), page).getInventory();

        List<String> options = new ArrayList<>(){{
            add("Back");
            add("PreviousPage");
            add("Return");
            add("NextPage");
            add("WhatIsThis.Cancelled/ExpiredItems");
        }};

        setOptions(options, config, inv);

        setPage(inv, page, items, ID, player);
    }

    public static void openBuying(@NotNull Player player, @NotNull String ID) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        if (!data.contains("Items." + ID)) {
            openShop(player, ShopType.SELL, shopCategory.get(player.getUniqueId()), 1);

            player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

            return;
        }

        Inventory inv = new AuctionMenu(9, Methods.color(config.getString("Settings.Buying-Item"))).getInventory();

        List<String> options = new ArrayList<>(){{
            add("Confirm");
            add("Cancel");
        }};

        for (String option : options) {
            String id = config.getString("Settings.GUISettings.OtherSettings." + option + ".Item");
            String name = config.getString("Settings.GUISettings.OtherSettings." + option + ".Name");
            ItemStack item;

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings.OtherSettings." + option + ".Lore")) {
                itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings." + option + ".Lore")).build();
            }

            item = itemBuilder.build();

            if (option.equals("Confirm")) {
                inv.setItem(0, item);
                inv.setItem(1, item);
                inv.setItem(2, item);
                inv.setItem(3, item);
            }

            if (option.equals("Cancel")) {
                inv.setItem(5, item);
                inv.setItem(6, item);
                inv.setItem(7, item);
                inv.setItem(8, item);
            }
        }


        String price = Methods.getPrice(ID, false);
        String time = Methods.convertToTime(data.getLong("Items." + ID + ".Time-Till-Expire"));

        OfflinePlayer target = null;

        String id = data.getString("Items." + ID + ".Seller");

        if (id != null) {
            target = Methods.getOfflinePlayer(id);
        }

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + ID + ".Item"));

        List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

        for (String l : config.getStringList("Settings.GUISettings.SellingItemLore")) {
            lore.add(l.replace("%Price%", price).replace("%price%", price)
                    .replace("%Seller%", target != null ? target.getName() : "N/A")
                    .replace("%seller%", target != null ? target.getName() : "N/A")
                    .replace("%Time%", time)
                    .replace("%time%", time));
        }

        itemBuilder.setLore(lore);

        inv.setItem(4, itemBuilder.build());

        IDs.put(player.getUniqueId(), ID);

        player.openInventory(inv);
    }

    public static void openBidding(@NotNull Player player, @NotNull String ID) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        if (!data.contains("Items." + ID)) {
            openShop(player, ShopType.BID, shopCategory.get(player.getUniqueId()), 1);

            player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

            return;
        }

        Inventory inv = new AuctionMenu(27, Methods.color(config.getString("Settings.Bidding-On-Item"))).getInventory();

        if (!bidding.containsKey(player.getUniqueId())) bidding.put(player.getUniqueId(), 0);

        inv.setItem(9, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&a+1").setAmount(1).build());
        inv.setItem(10, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&a+10").setAmount(1).build());
        inv.setItem(11, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&a+100").setAmount(1).build());
        inv.setItem(12, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&a+1000").setAmount(1).build());
        inv.setItem(14, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&c-1000").setAmount(1).build());
        inv.setItem(15, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&c-100").setAmount(1).build());
        inv.setItem(16, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&c-10").setAmount(1).build());
        inv.setItem(17, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&c-1").setAmount(1).build());
        inv.setItem(13, getBiddingGlass(player, ID));

        inv.setItem(22, new ItemBuilder().setMaterial(config.getString("Settings.GUISettings.OtherSettings.Bid.Item")).setAmount(1)
                .setName(config.getString("Settings.GUISettings.OtherSettings.Bid.Name")).setLore(config.getStringList("Settings.GUISettings.OtherSettings.Bid.Lore")).build());

        inv.setItem(4, getBiddingItem(ID));

        player.openInventory(inv);
    }

    public static void openViewer(@NotNull Player player, @NotNull String other, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();

        if (!Methods.isUUID(other)) other = String.valueOf(plugin.getServer().getPlayerUniqueId(other));

        if (!data.contains("Items")) {
            data.set("Items.Clear", null);

            Files.data.save();
        }

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                if (Objects.equals(data.getString("Items." + i + ".Seller"), other)) {
                    String price = Methods.getPrice(i, false);
                    String time = Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"));

                    OfflinePlayer target = null;

                    String id = data.getString("Items." + i + ".Seller");

                    if (id != null) {
                        target = Methods.getOfflinePlayer(id);
                    }

                    OfflinePlayer bidder = null;

                    String bid = data.getString("Items." + i + ".TopBidder");

                    if (bid != null && !bid.equals("None")) {
                        bidder = Methods.getOfflinePlayer(bid);
                    }

                    ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + i + ".Item"));

                    List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

                    if (data.getBoolean("Items." + i + ".Biddable")) {
                        for (String l : config.getStringList("Settings.GUISettings.Bidding")) {
                            lore.add(l.replace("%TopBid%", price)
                                    .replace("%topbid%", price)
                                    .replace("%Seller%", target != null ? target.getName() : "N/A")
                                    .replace("%seller%", target != null ? target.getName() : "N/A")
                                    .replace("%TopBidder%", bidder != null ? bidder.getName() : "N/A")
                                    .replace("%topbidder%", bidder != null ? bidder.getName() : "N/A")
                                    .replace("%Time%", time)
                                    .replace("%time%", time));
                        }
                    } else {
                        for (String l : config.getStringList("Settings.GUISettings.SellingItemLore")) {
                            lore.add(l.replace("%Price%", price)
                                    .replace("%price%", price)
                                    .replace("%Seller%", target != null ? target.getName() : "N/A")
                                    .replace("%seller%", target != null ? target.getName() : "N/A")
                                    .replace("%Time%", time)
                                    .replace("%time%", time));
                        }
                    }

                    itemBuilder.setLore(lore);

                    items.add(itemBuilder.build());

                    ID.add(data.getInt("Items." + i + ".StoreID"));
                }
            }
        }

        int maxPage = Methods.getMaxPage(items);

        page = Math.min(maxPage, page);

        Inventory inv = new AuctionMenu(54, Methods.color(config.getString("Settings.GUIName") + " #" + page), page).getInventory();

        List<String> options = new ArrayList<>();

        options.add("WhatIsThis.Viewing");

        setOptions(options, config, inv);

        setPage(inv, page, items, ID, player);
    }

    private static void setOptions(@NotNull List<String> options, @NotNull FileConfiguration config, @NotNull Inventory inv) {
        for (String option : options) {
            if (config.contains("Settings.GUISettings.OtherSettings." + option + ".Toggle")) {
                if (!config.getBoolean("Settings.GUISettings.OtherSettings." + option + ".Toggle")) {
                    continue;
                }
            }

            String id = config.getString("Settings.GUISettings.OtherSettings." + option + ".Item");
            String name = config.getString("Settings.GUISettings.OtherSettings." + option + ".Name");
            int slot = config.getInt("Settings.GUISettings.OtherSettings." + option + ".Slot");

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings.OtherSettings." + option + ".Lore")) {
                itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings." + option + ".Lore"));
            }

            inv.setItem(slot - 1, itemBuilder.build());
        }
    }

    private static ItemStack getBiddingGlass(@NotNull Player player, @NotNull String ID) {
        FileConfiguration config = Files.config.getConfiguration();

        String id = config.getString("Settings.GUISettings.OtherSettings.Bidding.Item");
        String name = config.getString("Settings.GUISettings.OtherSettings.Bidding.Name");

        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

        int bid = bidding.get(player.getUniqueId());

        String price = Methods.getPrice(ID, false);

        if (config.contains("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
            List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

            for (String l : config.getStringList("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
                lore.add(l.replace("%Bid%", String.valueOf(bid))
                        .replace("%bid%", String.valueOf(bid))
                        .replace("%TopBid%", price)
                        .replace("%topbid%", price));
            }

            itemBuilder.setLore(lore);
        }

        return itemBuilder.build();
    }

    private static ItemStack getBiddingItem(@NotNull String ID) {
        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        ItemStack item = Methods.fromBase64(data.getString("Items." + ID + ".Item"));

        String price = Methods.getPrice(ID, false);
        String time = Methods.convertToTime(data.getLong("Items." + ID + ".Time-Till-Expire"));

        OfflinePlayer target = null;

        String id = data.getString("Items." + ID + ".Seller");

        if (id != null) {
            target = Methods.getOfflinePlayer(id);
        }

        OfflinePlayer bidder = null;

        String bid = data.getString("Items." + ID + ".TopBidder");

        if (bid != null && !bid.equals("None")) {
            bidder = Methods.getOfflinePlayer(bid);
        }

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(item);

        List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

        for (String l : config.getStringList("Settings.GUISettings.Bidding")) {
            lore.add(l.replace("%TopBid%", price)
                    .replace("%topbid%", price)
                    .replace("%Seller%", target != null ? target.getName() : "N/A").replace("%seller%", target != null ? target.getName() : "N/A")
                    .replace("%TopBidder%", bidder != null ? bidder.getName() : "N/A").replace("%topbidder%", bidder != null ? bidder.getName() : "N/A")
                    .replace("%Time%", time)
                    .replace("%time%", time));
        }

        itemBuilder.setLore(lore);

        return itemBuilder.build();
    }

    private static void playClick(@NotNull Player player) {
        FileConfiguration config = Files.config.getConfiguration();

        if (config.getBoolean("Settings.Sounds.Toggle", false)) {
            String sound = config.getString("Settings.Sounds.Sound", "");

            Sound soundToPlay = Registry.SOUNDS.get(NamespacedKey.minecraft(sound));

            if (soundToPlay == null) return;

            player.playSound(player.getLocation(), soundToPlay, 1, 1);
        }
    }

    private void playSoldSound(@NotNull Player player) {
        FileConfiguration config = Files.config.getConfiguration();

        String sound = config.getString("Settings.Sold-Item-Sound", "");

        Sound soundToPlay = Registry.SOUNDS.get(NamespacedKey.minecraft(sound));

        if (soundToPlay == null) return;

        player.playSound(player.getLocation(), soundToPlay, 1, 1);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof  AuctionMenu auctionMenu)) return;
        FileConfiguration config = Files.config.getConfiguration();

        Player player = (Player) event.getPlayer();

        if (auctionMenu.getTitle().contains(Methods.color(config.getString("Settings.Bidding-On-Item")))) bidding.remove(player);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent clickEvent) {
        if (!(clickEvent.getInventory().getHolder() instanceof  AuctionMenu auctionMenu)) return;
        clickEvent.setCancelled(true);

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        Player player = (Player) clickEvent.getWhoClicked();
        final Inventory inv = clickEvent.getClickedInventory();

        int slot = clickEvent.getRawSlot();

        if (inv == null) return;

        if (slot > inv.getSize()) return;

        ItemStack item = clickEvent.getCurrentItem();

        if (item == null) return;
        
        if (!item.hasItemMeta()) return;

        if (auctionMenu.getTitle().contains(config.getString("Settings.Categories"))) {

            if (item.getItemMeta().hasDisplayName()) {

                String displayName = item.getItemMeta().getDisplayName();

                for (Category cat : Category.values()) {
                    if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.Category-Settings." + cat.getName() + ".Name")))) {
                        openShop(player, shopType.get(player.getUniqueId()), cat, 1);

                        playClick(player);

                        return;
                    }

                    if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Back.Name")))) {
                        openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                        playClick(player);

                        return;
                    }
                }
            }
        }

        if (auctionMenu.getTitle().contains(config.getString("Settings.Bidding-On-Item"))) {

            if (item.getItemMeta().hasDisplayName()) {

                String displayName = item.getItemMeta().getDisplayName();

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bid.Name")))) {
                    String ID = biddingID.get(player.getUniqueId());
                    int bid = bidding.get(player.getUniqueId());
                    String topBidder = data.getString("Items." + ID + ".TopBidder");

                    if (plugin.getSupport().getMoney(player) < bid) {
                        Map<String, String> placeholders = new HashMap<>();

                        placeholders.put("%Money_Needed%", (bid - plugin.getSupport().getMoney(player)) + "");
                        placeholders.put("%money_needed%", (bid - plugin.getSupport().getMoney(player)) + "");

                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                        return;
                    }

                    if (data.getLong("Items." + ID + ".Price") > bid) {
                        player.sendMessage(Messages.BID_MORE_MONEY.getMessage(player));

                        return;
                    }

                    if (data.getLong("Items." + ID + ".Price") >= bid && !topBidder.equalsIgnoreCase("None")) {
                        player.sendMessage(Messages.BID_MORE_MONEY.getMessage(player));

                        return;
                    }

                    new AuctionNewBidEvent(player, Methods.fromBase64(data.getString("Items." + ID + ".Item")), bid).callEvent();

                    data.set("Items." + ID + ".Price", bid);
                    data.set("Items." + ID + ".TopBidder", player.getUniqueId().toString());
                    data.set("Items." + ID + ".TopBidderName", player.getName());

                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Bid%", bid + "");

                    player.sendMessage(Messages.BID_MESSAGE.getMessage(player, placeholders));

                    Files.data.save();

                    bidding.put(player.getUniqueId(), 0);
                    player.closeInventory();
                    playClick(player);
                    return;
                }

                Map<String, Integer> priceEdits = new HashMap<>();
                priceEdits.put("&a+1", 1);
                priceEdits.put("&a+10", 10);
                priceEdits.put("&a+100", 100);
                priceEdits.put("&a+1000", 1000);
                priceEdits.put("&c-1", -1);
                priceEdits.put("&c-10", -10);
                priceEdits.put("&c-100", -100);
                priceEdits.put("&c-1000", -1000);

                for (String price : priceEdits.keySet()) {
                    if (displayName.equals(Methods.color(price))) {
                        try {
                            bidding.put(player.getUniqueId(), (bidding.get(player.getUniqueId()) + priceEdits.get(price)));

                            inv.setItem(4, getBiddingItem(biddingID.get(player.getUniqueId())));

                            inv.setItem(13, getBiddingGlass(player, biddingID.get(player.getUniqueId())));

                            playClick(player);

                            return;
                        } catch (Exception ex) {
                            player.closeInventory();

                            player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

                            return;
                        }
                    }
                }
            }
        }

        if (auctionMenu.getTitle().contains(config.getString("Settings.GUIName"))) {

            if (item.getItemMeta().hasDisplayName()) {

                String displayName = item.getItemMeta().getDisplayName();

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.NextPage.Name")))) {
                    Methods.updateAuction();

                    int page = auctionMenu.getPageNumber();

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), page + 1);

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.PreviousPage.Name")))) {
                    Methods.updateAuction();

                    int page = auctionMenu.getPageNumber();

                    if (page == 1) page++;

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), page - 1);

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Refresh.Name")))) {
                    Methods.updateAuction();

                    int page = auctionMenu.getPageNumber();

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), page);

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bidding/Selling.Selling.Name")))) {
                    openShop(player, ShopType.BID, shopCategory.get(player.getUniqueId()), 1);

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bidding/Selling.Bidding.Name")))) {
                    openShop(player, ShopType.SELL, shopCategory.get(player.getUniqueId()), 1);

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cancelled/ExpiredItems.Name")))) {
                    openPlayersExpiredList(player, 1);

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.SellingItems.Name")))) {
                    openPlayersCurrentList(player, 1);

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Category1.Name")))) {
                    openCategories(player, shopType.get(player.getUniqueId()));

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Category2.Name")))) {
                    openCategories(player, shopType.get(player.getUniqueId()));

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Your-Item.Name")))) {
                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Name")))) {
                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Name")))) {
                    return;
                }
            }

            if (List.containsKey(player.getUniqueId())) {
                if (List.get(player.getUniqueId()).size() >= slot) {
                    int id = List.get(player.getUniqueId()).get(slot);

                    if (data.contains("Items")) {
                        for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                            int ID = data.getInt("Items." + i + ".StoreID");

                            if (id == ID) {
                                if (player.hasPermission("crazyauctions.admin") || player.hasPermission("crazyauctions.force-end")) {
                                    if (clickEvent.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                        int num = 1;
                                        for (; data.contains("OutOfTime/Cancelled." + num); num++) ;

                                        String seller = data.getString("Items." + i + ".Seller");
                                        Player sellerPlayer = Methods.getPlayer(seller);

                                        if (Methods.isOnline(seller) && sellerPlayer != null) {
                                            sellerPlayer.sendMessage(Messages.ADMIN_FORCE_CANCELLED_TO_PLAYER.getMessage(player));
                                        }

                                        AuctionCancelledEvent event = new AuctionCancelledEvent((sellerPlayer != null ? sellerPlayer : Methods.getOfflinePlayer(seller)), Methods.fromBase64(data.getString("Items." + i + ".Item")), Reasons.ADMIN_FORCE_CANCEL);
                                        event.callEvent();

                                        data.set("OutOfTime/Cancelled." + num + ".Seller", data.getString("Items." + i + ".Seller"));
                                        data.set("OutOfTime/Cancelled." + num + ".Full-Time", data.getLong("Items." + i + ".Full-Time"));
                                        data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
                                        data.set("OutOfTime/Cancelled." + num + ".Item", data.getString("Items." + i + ".Item"));
                                        data.set("Items." + i, null);

                                        Files.data.save();

                                        player.sendMessage(Messages.ADMIN_FORCE_CANCELLED.getMessage(player));

                                        playClick(player);

                                        int page = auctionMenu.getPageNumber();

                                        openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), page);

                                        return;
                                    }
                                }

                                if (Objects.equals(data.getString("Items." + i + ".Seller"), player.getUniqueId().toString())) {
                                    String itemName = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Item");
                                    String name = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Name");

                                    ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                                    if (config.contains("Settings.GUISettings.OtherSettings.Your-Item.Lore")) {
                                        itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings.Your-Item.Lore"));
                                    }

                                    inv.setItem(slot, itemBuilder.build());

                                    playClick(player);

                                    new FoliaRunnable(plugin.getServer().getGlobalRegionScheduler()) {
                                        @Override
                                        public void run() {
                                            inv.setItem(slot, item);
                                        }
                                    }.runDelayed(plugin, 3 * 20);

                                    return;
                                }

                                long cost = data.getLong("Items." + i + ".Price");

                                if (plugin.getSupport().getMoney(player) < cost) {
                                    String itemName = config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Item");
                                    String name = config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Name");

                                    ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                                    if (config.contains("Settings.GUISettings.OtherSettings.Cant-Afford.Lore")) {
                                        itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings.Cant-Afford.Lore"));
                                    }

                                    inv.setItem(slot, itemBuilder.build());
                                    playClick(player);

                                    new FoliaRunnable(plugin.getServer().getGlobalRegionScheduler()) {
                                        @Override
                                        public void run() {
                                            inv.setItem(slot, item);
                                        }
                                    }.runDelayed(plugin, 3 * 20);

                                    return;
                                }

                                if (data.getBoolean("Items." + i + ".Biddable")) {
                                    if (Objects.equals(player.getUniqueId().toString(), data.getString("Items." + i + ".TopBidder"))) {
                                        String itemName = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Item");
                                        String name = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Name");

                                        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                                        if (config.contains("Settings.GUISettings.OtherSettings.Top-Bidder.Lore")) {
                                            itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings.Top-Bidder.Lore"));
                                        }

                                        inv.setItem(slot, itemBuilder.build());

                                        playClick(player);

                                        new FoliaRunnable(plugin.getServer().getGlobalRegionScheduler()) {
                                            @Override
                                            public void run() {
                                                inv.setItem(slot, item);
                                            }
                                        }.runDelayed(plugin, 3 * 20);

                                        return;
                                    }

                                    playClick(player);

                                    openBidding(player, i);

                                    biddingID.put(player.getUniqueId(), i);
                                } else {
                                    playClick(player);

                                    openBuying(player, i);
                                }

                                return;
                            }
                        }
                    }

                    playClick(player);

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                    player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

                    return;
                }
            }
        }

        if (auctionMenu.getTitle().contains(config.getString("Settings.Buying-Item"))) {

            if (item.getItemMeta().hasDisplayName()) {

                String displayName = item.getItemMeta().getDisplayName();

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Confirm.Name")))) {
                    String ID = IDs.get(player.getUniqueId());
                    long cost = data.getLong("Items." + ID + ".Price");
                    String seller = data.getString("Items." + ID + ".Seller");

                    if (!data.contains("Items." + ID)) {
                        playClick(player);

                        openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

                        return;
                    }

                    if (Methods.isInvFull(player)) {
                        playClick(player);

                        player.closeInventory();
                        player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

                        return;
                    }

                    final VaultSupport support = plugin.getSupport();

                    Map<String, String> placeholders = new HashMap<>();

                    if (support.getMoney(player) < cost) {
                        playClick(player);

                        player.closeInventory();

                        placeholders.put("%Money_Needed%", (cost - plugin.getSupport().getMoney(player)) + "");
                        placeholders.put("%money_needed%", (cost - plugin.getSupport().getMoney(player)) + "");

                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                        return;
                    }

                    ItemStack i = Methods.fromBase64(data.getString("Items." + ID + ".Item"));

                    new AuctionBuyEvent(player, i, cost).callEvent();

                    if (!support.removeMoney(player, cost)) {
                        playClick(player);

                        player.closeInventory();

                        placeholders.put("%Money_Needed%", (cost - support.getMoney(player)) + "");
                        placeholders.put("%money_needed%", (cost - support.getMoney(player)) + "");

                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                        return;
                    }

                    support.addMoney(Methods.getOfflinePlayer(seller), cost);

                    String price = Methods.getPrice(ID, false);

                    placeholders.put("%Price%", price);
                    placeholders.put("%price%", price);
                    placeholders.put("%Player%", player.getName());
                    placeholders.put("%player%", player.getName());

                    player.sendMessage(Messages.BOUGHT_ITEM.getMessage(player, placeholders));

                    //if (seller != null && Methods.isOnline(seller) && Methods.getPlayer(seller) != null) {
                    final Player auctioneer = Methods.getPlayer(seller);

                    if (auctioneer != null) {
                        auctioneer.sendMessage(Messages.PLAYER_BOUGHT_ITEM.getMessage(player, placeholders));
                        playSoldSound(auctioneer);
                    }

                    player.getInventory().addItem(i);

                    data.set("Items." + ID, null);
                    Files.data.save();

                    playClick(player);

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cancel.Name")))) {
                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                    playClick(player);

                    return;
                }
            }
        }

        if (auctionMenu.getTitle().contains(config.getString("Settings.Players-Current-Items"))) {

            if (item.getItemMeta().hasDisplayName()) {
                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Back.Name")))) {
                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                    playClick(player);

                    return;
                }
            }

            if (List.containsKey(player.getUniqueId())) {
                if (List.get(player.getUniqueId()).size() >= slot) {
                    int id = List.get(player.getUniqueId()).get(slot);

                    if (data.contains("Items")) {
                        for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                            int ID = data.getInt("Items." + i + ".StoreID");
                            if (id == ID) {
                                player.sendMessage(Messages.CANCELLED_ITEM.getMessage(player));

                                AuctionCancelledEvent event = new AuctionCancelledEvent(player, Methods.fromBase64(data.getString("Items." + i + ".Item")), Reasons.PLAYER_FORCE_CANCEL);
                                event.callEvent();

                                int num = 1;
                                while (data.contains("OutOfTime/Cancelled." + num)) num++;

                                data.set("OutOfTime/Cancelled." + num + ".Seller", data.getString("Items." + i + ".Seller"));
                                data.set("OutOfTime/Cancelled." + num + ".Full-Time", data.getLong("Items." + i + ".Full-Time"));
                                data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
                                data.set("OutOfTime/Cancelled." + num + ".Item", data.getString("Items." + i + ".Item"));

                                data.set("Items." + i, null);

                                Files.data.save();

                                playClick(player);

                                openPlayersCurrentList(player, 1);

                                return;
                            }
                        }
                    }

                    playClick(player);

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                    player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

                    return;
                }
            }
        }

        if (auctionMenu.getTitle().contains(config.getString("Settings.Cancelled/Expired-Items"))) {

            if (item.getItemMeta().hasDisplayName()) {

                String displayName = item.getItemMeta().getDisplayName();

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Back.Name")))) {
                    Methods.updateAuction();

                    playClick(player);

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.PreviousPage.Name")))) {
                    Methods.updateAuction();

                    int page = auctionMenu.getPageNumber();

                    if (page == 1) page++;

                    playClick(player);

                    openPlayersExpiredList(player, (page - 1));

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Return.Name")))) {
                    Methods.updateAuction();

                    int page = auctionMenu.getPageNumber();

                    if (data.contains("OutOfTime/Cancelled")) {
                        for (String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                            if (Objects.equals(data.getString("OutOfTime/Cancelled." + i + ".Seller"), player.getUniqueId().toString())) {
                                if (Methods.isInvFull(player)) {
                                    player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

                                    break;
                                } else {
                                    player.getInventory().addItem(Methods.fromBase64(data.getString("OutOfTime/Cancelled." + i + ".Item")));

                                    data.set("OutOfTime/Cancelled." + i, null);
                                }
                            }
                        }
                    }

                    player.sendMessage(Messages.GOT_ITEM_BACK.getMessage(player));

                    Files.data.save();

                    playClick(player);

                    openPlayersExpiredList(player, page);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.NextPage.Name")))) {
                    Methods.updateAuction();

                    int page = auctionMenu.getPageNumber();

                    playClick(player);

                    openPlayersExpiredList(player, (page + 1));

                    return;
                }
            }

            if (List.containsKey(player.getUniqueId())) {
                if (List.get(player.getUniqueId()).size() >= slot) {
                    int id = List.get(player.getUniqueId()).get(slot);

                    if (data.contains("OutOfTime/Cancelled")) {
                        for (String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                            int ID = data.getInt("OutOfTime/Cancelled." + i + ".StoreID");

                            if (id == ID) {
                                if (!Methods.isInvFull(player)) {
                                    player.sendMessage(Messages.GOT_ITEM_BACK.getMessage(player));

                                    player.getInventory().addItem(Methods.fromBase64(data.getString("OutOfTime/Cancelled." + i + ".Item")));

                                    data.set("OutOfTime/Cancelled." + i, null);

                                    Files.data.save();

                                    playClick(player);

                                    openPlayersExpiredList(player, 1);
                                } else {
                                    player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));
                                }

                                return;
                            }
                        }
                    }

                    playClick(player);

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                    player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));
                }
            }
        }
    }
}