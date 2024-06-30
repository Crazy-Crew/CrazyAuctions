package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.*;
import com.badbones69.crazyauctions.api.FileManager.Files;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.Reasons;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.events.AuctionBuyEvent;
import com.badbones69.crazyauctions.api.events.AuctionCancelledEvent;
import com.badbones69.crazyauctions.api.events.AuctionNewBidEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class GuiListener implements Listener {
    
    private static final CrazyAuctions plugin = CrazyAuctions.get();
    private static final CrazyManager crazyManager = plugin.getCrazyManager();
    
    private static final HashMap<UUID, Integer> bidding = new HashMap<>();
    private static final HashMap<UUID, String> biddingID = new HashMap<>();
    private static final HashMap<UUID, ShopType> shopType = new HashMap<>(); // Shop Type
    private static final HashMap<UUID, Category> shopCategory = new HashMap<>(); // Category Type
    private static final HashMap<UUID, List<Integer>> List = new HashMap<>();
    private static final HashMap<UUID, String> IDs = new HashMap<>();
    
    public static void openShop(Player player, ShopType sell, Category cat, int page) {
        Methods.updateAuction();
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();

        if (!data.contains("Items")) {
            data.set("Items.Clear", null);
            Files.DATA.saveFile();
        }

        if (cat != null) {
            shopCategory.put(player.getUniqueId(), cat);
        } else {
            shopCategory.put(player.getUniqueId(), Category.NONE);
        }

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                List<String> lore = new ArrayList<>();

                ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + i + ".Item"));

                if (itemBuilder != null && data.contains("Items." + i + ".Item") && (cat.getItems().contains(itemBuilder.getItemStack().getType()) || cat == Category.NONE)) {
                    if (data.getBoolean("Items." + i + ".Biddable")) {
                        if (sell == ShopType.BID) {
                            String seller = data.getString("Items." + i + ".Seller");

                            OfflinePlayer target = null;

                            if (seller != null) {
                                target = Methods.getOfflinePlayer(seller);
                            }

                            String price = Methods.getPrice(i, false);
                            String time = Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"));

                            OfflinePlayer bidder = null;

                            String topbidder = data.getString("Items." + i + ".TopBidder");

                            if (topbidder != null && !topbidder.equals("None")) {
                                bidder = Methods.getOfflinePlayer(topbidder);
                            }

                            for (String key : config.getStringList("Settings.GUISettings.Bidding")) {
                                String line = key.replace("%TopBid%", price).replace("%topbid%", price);

                                line = target != null ? line.replace("%Seller%", target.getName()).replace("%seller%", target.getName()) : line.replace("%Seller%", "N/A").replace("%seller%", "N/A");

                                line = bidder != null ? line.replace("%TopBidder%", bidder.getName()).replace("%topbidder%", bidder.getName()) : line.replace("%TopBidder%", "N/A").replace("%topbidder%", "N/A");

                                lore.add(line.replace("%Time%", time).replace("%time%", time));
                            }

                            itemBuilder.setLore(lore);

                            items.add(itemBuilder.build());

                            ID.add(data.getInt("Items." + i + ".StoreID"));
                        }
                    } else {
                        if (sell == ShopType.SELL) {
                            String seller = data.getString("Items." + i + ".Seller");

                            OfflinePlayer target = null;

                            if (seller != null) {
                                target = Methods.getOfflinePlayer(seller);
                            }

                            String price = Methods.getPrice(i, false);
                            String time = Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"));

                            String format = String.format(Locale.ENGLISH, "%,d", Long.parseLong(price));

                            for (String l : config.getStringList("Settings.GUISettings.SellingItemLore")) {
                                lore.add(l.replace("%Price%", format).replace("%price%", format)
                                        .replace("%Seller%", target != null ? target.getName() : "N/A").replace("%seller%", target != null ? target.getName() : "N/A")
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

        int maxPage = Methods.getMaxPage(items);
        for (; page > maxPage; page--);

        Inventory inv = plugin.getServer().createInventory(null, 54, Methods.color(config.getString("Settings.GUIName") + " #" + page));
        List<String> options = new ArrayList<>();

        options.add("SellingItems");
        options.add("Cancelled/ExpiredItems");
        options.add("PreviousPage");
        options.add("Refesh");
        options.add("NextPage");
        options.add("Category1");
        options.add("Category2");

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

        for (String o : options) {
            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Toggle")) {
                if (!config.getBoolean("Settings.GUISettings.OtherSettings." + o + ".Toggle")) {
                    continue;
                }
            }

            String id = config.getString("Settings.GUISettings.OtherSettings." + o + ".Item");
            String name = config.getString("Settings.GUISettings.OtherSettings." + o + ".Name");
            List<String> lore = new ArrayList<>();
            int slot = config.getInt("Settings.GUISettings.OtherSettings." + o + ".Slot");
            String cName = Methods.color(config.getString("Settings.GUISettings.Category-Settings." + shopCategory.get(player.getUniqueId()).getName() + ".Name"));

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                for (String l : config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                    lore.add(l.replace("%Category%", cName).replace("%category%", cName));
                }

                inv.setItem(slot - 1, itemBuilder.setLore(lore).build());
            } else {
                inv.setItem(slot - 1, itemBuilder.setLore(lore).build());
            }
        }

        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
        }

        List<Integer> Id = new ArrayList<>(Methods.getPageInts(ID, page));
        List.put(player.getUniqueId(), Id);
        player.openInventory(inv);
    }
    
    public static void openCategories(Player player, ShopType shop) {
        Methods.updateAuction();
        FileConfiguration config = Files.CONFIG.getFile();

        Inventory inv = plugin.getServer().createInventory(null, 54, Methods.color(config.getString("Settings.Categories")));
        List<String> options = new ArrayList<>();

        options.add("OtherSettings.Back");
        options.add("OtherSettings.WhatIsThis.Categories");
        options.add("Category-Settings.Armor");
        options.add("Category-Settings.Weapons");
        options.add("Category-Settings.Tools");
        options.add("Category-Settings.Food");
        options.add("Category-Settings.Potions");
        options.add("Category-Settings.Blocks");
        options.add("Category-Settings.Other");
        options.add("Category-Settings.None");

        for (String o : options) {
            if (config.contains("Settings.GUISettings." + o + ".Toggle")) {
                if (!config.getBoolean("Settings.GUISettings." + o + ".Toggle")) {
                    continue;
                }
            }

            String id = config.getString("Settings.GUISettings." + o + ".Item");
            String name = config.getString("Settings.GUISettings." + o + ".Name");
            int slot = config.getInt("Settings.GUISettings." + o + ".Slot");

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings." + o + ".Lore")) {
                itemBuilder.setLore(config.getStringList("Settings.GUISettings." + o + ".Lore"));
            }

            inv.setItem(slot - 1, itemBuilder.build());
        }

        shopType.put(player.getUniqueId(), shop);
        player.openInventory(inv);
    }
    
    public static void openPlayersCurrentList(Player player, int page) {
        Methods.updateAuction();
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();

        Inventory inv = plugin.getServer().createInventory(null, 54, Methods.color(config.getString("Settings.Players-Current-Items")));

        List<String> options = new ArrayList<>();
        options.add("Back");
        options.add("WhatIsThis.CurrentItems");

        for (String o : options) {
            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Toggle")) {
                if (!config.getBoolean("Settings.GUISettings.OtherSettings." + o + ".Toggle")) {
                    continue;
                }
            }

            String id = config.getString("Settings.GUISettings.OtherSettings." + o + ".Item");
            String name = config.getString("Settings.GUISettings.OtherSettings." + o + ".Name");
            int slot = config.getInt("Settings.GUISettings.OtherSettings." + o + ".Slot");

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore"));
            }

            inv.setItem(slot - 1, itemBuilder.build());
        }

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                if (data.getString("Items." + i + ".Seller").equalsIgnoreCase(player.getUniqueId().toString())) {
                    List<String> lore = new ArrayList<>();

                    String price = Methods.getPrice(i, false);
                    String time = Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"));

                    for (String l : config.getStringList("Settings.GUISettings.CurrentLore")) {
                        lore.add(l.replace("%Price%", price)
                                .replace("%price%", price)
                                .replace("%Time%", time)
                                .replace("%time%", time));
                    }

                    ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + i + ".Item"));

                    itemBuilder.setLore(lore);

                    items.add(itemBuilder.build());

                    ID.add(data.getInt("Items." + i + ".StoreID"));
                }
            }
        }

        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
        }

        List<Integer> Id = new ArrayList<>(Methods.getPageInts(ID, page));
        List.put(player.getUniqueId(), Id);
        player.openInventory(inv);
    }
    
    public static void openPlayersExpiredList(Player player, int page) {
        Methods.updateAuction();
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();

        if (data.contains("OutOfTime/Cancelled")) {
            for (String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                if (data.getString("OutOfTime/Cancelled." + i + ".Seller") != null) {
                    if (data.getString("OutOfTime/Cancelled." + i + ".Seller").equalsIgnoreCase(player.getUniqueId().toString())) {
                        List<String> lore = new ArrayList<>();

                        String price = Methods.getPrice(i, true);
                        String time = Methods.convertToTime(data.getLong("OutOfTime/Cancelled." + i + ".Full-Time"));

                        for (String l : config.getStringList("Settings.GUISettings.Cancelled/ExpiredLore")) {
                            lore.add(l.replace("%Price%", price)
                                    .replace("%price%", price)
                                    .replace("%Time%", time)
                                    .replace("%time%", time));
                        }

                        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("OutOfTime/Cancelled." + i + ".Item"));

                        itemBuilder.setLore(lore);

                        items.add(itemBuilder.build());

                        ID.add(data.getInt("OutOfTime/Cancelled." + i + ".StoreID"));
                    }
                }
            }
        }

        int maxPage = Methods.getMaxPage(items);
        for (; page > maxPage; page--);

        Inventory inv = plugin.getServer().createInventory(null, 54, Methods.color(config.getString("Settings.Cancelled/Expired-Items") + " #" + page));
        List<String> options = new ArrayList<>();

        options.add("Back");
        options.add("PreviousPage");
        options.add("Return");
        options.add("NextPage");
        options.add("WhatIsThis.Cancelled/ExpiredItems");

        for (String o : options) {
            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Toggle")) {
                if (!config.getBoolean("Settings.GUISettings.OtherSettings." + o + ".Toggle")) {
                    continue;
                }
            }

            String id = config.getString("Settings.GUISettings.OtherSettings." + o + ".Item");
            String name = config.getString("Settings.GUISettings.OtherSettings." + o + ".Name");
            int slot = config.getInt("Settings.GUISettings.OtherSettings." + o + ".Slot");

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore"));
            }

            inv.setItem(slot - 1, itemBuilder.build());
        }

        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
        }

        List<Integer> Id = new ArrayList<>(Methods.getPageInts(ID, page));
        List.put(player.getUniqueId(), Id);
        player.openInventory(inv);
    }
    
    public static void openBuying(Player player, String ID) {
        Methods.updateAuction();

        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();

        if (!data.contains("Items." + ID)) {
            openShop(player, ShopType.SELL, shopCategory.get(player.getUniqueId()), 1);
            player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage());
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 9, Methods.color(config.getString("Settings.Buying-Item")));

        List<String> options = new ArrayList<>();
        options.add("Confirm");
        options.add("Cancel");

        for (String o : options) {
            String id = config.getString("Settings.GUISettings.OtherSettings." + o + ".Item");
            String name = config.getString("Settings.GUISettings.OtherSettings." + o + ".Name");
            ItemStack item;

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore")).build();
            }

            item = itemBuilder.build();

            if (o.equals("Confirm")) {
                inv.setItem(0, item);
                inv.setItem(1, item);
                inv.setItem(2, item);
                inv.setItem(3, item);
            }

            if (o.equals("Cancel")) {
                inv.setItem(5, item);
                inv.setItem(6, item);
                inv.setItem(7, item);
                inv.setItem(8, item);
            }
        }

        List<String> lore = new ArrayList<>();

        String price = Methods.getPrice(ID, false);
        String time = Methods.convertToTime(data.getLong("Items." + ID + ".Time-Till-Expire"));

        OfflinePlayer target = null;

        String id = data.getString("Items." + ID + ".Seller");

        if (id != null) {
            target = Methods.getOfflinePlayer(id);
        }

        for (String l : config.getStringList("Settings.GUISettings.SellingItemLore")) {
            lore.add(l.replace("%Price%", price).replace("%price%", price)
                    .replace("%Seller%", target != null ? target.getName() : "N/A")
                    .replace("%seller%", target != null ? target.getName() : "N/A")
                    .replace("%Time%", time)
                    .replace("%time%", time));
        }

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + ID + ".Item"));

        itemBuilder.setLore(lore);

        inv.setItem(4, itemBuilder.build());

        IDs.put(player.getUniqueId(), ID);
        player.openInventory(inv);
    }
    
    public static void openBidding(Player player, String ID) {
        Methods.updateAuction();
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();

        if (!data.contains("Items." + ID)) {
            openShop(player, ShopType.BID, shopCategory.get(player.getUniqueId()), 1);
            player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage());
            return;
        }

        Inventory inv = plugin.getServer().createInventory(null, 27, Methods.color(config.getString("Settings.Bidding-On-Item")));
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
    
    public static void openViewer(Player player, String other, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();

        if (!data.contains("Items")) {
            data.set("Items.Clear", null);
            Files.DATA.saveFile();
        }

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                if (data.getString("Items." + i + ".Seller").equalsIgnoreCase(other)) {
                    List<String> lore = new ArrayList<>();

                    String price = Methods.getPrice(i, false);
                    String time = Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"));

                    OfflinePlayer target = null;

                    String id = data.getString("Items." + i + ".Seller");

                    if (id != null) {
                        target = Methods.getOfflinePlayer(id);
                    }

                    OfflinePlayer bidder = null;

                    String bid = data.getString("Items." + i + ".TopBidder");

                    if (id != null) {
                        bidder = Methods.getOfflinePlayer(bid);
                    }

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

                    ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + ID + ".Item"));

                    itemBuilder.setLore(lore);

                    items.add(itemBuilder.build());

                    ID.add(data.getInt("Items." + i + ".StoreID"));
                }
            }
        }

        int maxPage = Methods.getMaxPage(items);
        for (; page > maxPage; page--);

        Inventory inv = plugin.getServer().createInventory(null, 54, Methods.color(config.getString("Settings.GUIName") + " #" + page));

        List<String> options = new ArrayList<>();
        options.add("WhatIsThis.Viewing");

        for (String o : options) {
            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Toggle")) {
                if (!config.getBoolean("Settings.GUISettings.OtherSettings." + o + ".Toggle")) {
                    continue;
                }
            }

            String id = config.getString("Settings.GUISettings.OtherSettings." + o + ".Item");
            String name = config.getString("Settings.GUISettings.OtherSettings." + o + ".Name");
            int slot = config.getInt("Settings.GUISettings.OtherSettings." + o + ".Slot");

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore"));
            }

            inv.setItem(slot - 1, itemBuilder.build());
        }

        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
        }

        List.put(player.getUniqueId(), new ArrayList<>(Methods.getPageInts(ID, page)));
        player.openInventory(inv);
    }
    
    private static ItemStack getBiddingGlass(Player player, String ID) {
        FileConfiguration config = Files.CONFIG.getFile();

        String id = config.getString("Settings.GUISettings.OtherSettings.Bidding.Item");
        String name = config.getString("Settings.GUISettings.OtherSettings.Bidding.Name");

        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

        int bid = bidding.get(player.getUniqueId());

        String price = Methods.getPrice(ID, false);

        if (config.contains("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
            List<String> lore = new ArrayList<>();
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
    
    private static ItemStack getBiddingItem(String ID) {
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        ItemStack item = Methods.fromBase64(data.getString("Items." + ID + ".Item"));
        List<String> lore = new ArrayList<>();

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

        for (String l : config.getStringList("Settings.GUISettings.Bidding")) {
            lore.add(l.replace("%TopBid%", price)
                    .replace("%topbid%", price)
                    .replace("%Seller%", target != null ? target.getName() : "N/A").replace("%seller%", target != null ? target.getName() : "N/A")
                    .replace("%TopBidder%", bidder != null ? bidder.getName() : "N/A").replace("%topbidder%", bidder != null ? bidder.getName() : "N/A")
                    .replace("%Time%", time)
                    .replace("%time%", time));
        }

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(item);

        itemBuilder.setLore(lore);

        return itemBuilder.build();
    }
    
    private static void playClick(Player player) {
        FileConfiguration config = Files.CONFIG.getFile();

        if (config.getBoolean("Settings.Sounds.Toggle", false)) {
            String sound = config.getString("Settings.Sounds.Sound");

            try {
                player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 1);
            } catch (Exception e) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1F);
            }
        }
    }

    private void playSoldSound(@NotNull Player player) {
        FileConfiguration config = Files.CONFIG.getFile();
        String sound = config.getString("Settings.Sold-Item-Sound", "");
        if (sound.isEmpty()) return;

        try {
            player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 1);
        } catch (Exception ignored) {}
    }
    
    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        FileConfiguration config = Files.CONFIG.getFile();
        Player player = (Player) e.getPlayer();

        if (e.getView().getTitle().contains(Methods.color(config.getString("Settings.Bidding-On-Item")))) bidding.remove(player);
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        Player player = (Player) e.getWhoClicked();
        final Inventory inv = e.getClickedInventory();

        if (inv != null) {
            if (e.getView().getTitle().contains(Methods.color(config.getString("Settings.Categories")))) {
                e.setCancelled(true);
                int slot = e.getRawSlot();

                if (slot <= inv.getSize()) {
                    if (e.getCurrentItem() != null) {
                        ItemStack item = e.getCurrentItem();

                        if (item.hasItemMeta()) {
                            if (item.getItemMeta().hasDisplayName()) {
                                for (Category cat : Category.values()) {
                                    if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.Category-Settings." + cat.getName() + ".Name")))) {
                                        openShop(player, shopType.get(player.getUniqueId()), cat, 1);
                                        playClick(player);
                                        return;
                                    }

                                    if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Back.Name")))) {
                                        openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);
                                        playClick(player);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (e.getView().getTitle().contains(Methods.color(config.getString("Settings.Bidding-On-Item")))) {
                e.setCancelled(true);
                int slot = e.getRawSlot();

                if (slot <= inv.getSize()) {
                    if (e.getCurrentItem() != null) {
                        ItemStack item = e.getCurrentItem();

                        if (item.hasItemMeta()) {
                            if (item.getItemMeta().hasDisplayName()) {
                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bid.Name")))) {
                                    String ID = biddingID.get(player.getUniqueId());
                                    int bid = bidding.get(player.getUniqueId());
                                    String topBidder = data.getString("Items." + ID + ".TopBidder");

                                    if (plugin.getSupport().getMoney(player) < bid) {
                                        HashMap<String, String> placeholders = new HashMap<>();
                                        placeholders.put("%Money_Needed%", (bid - plugin.getSupport().getMoney(player)) + "");
                                        placeholders.put("%money_needed%", (bid - plugin.getSupport().getMoney(player)) + "");
                                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
                                        return;
                                    }

                                    if (data.getLong("Items." + ID + ".Price") > bid) {
                                        player.sendMessage(Messages.BID_MORE_MONEY.getMessage());
                                        return;
                                    }

                                    if (data.getLong("Items." + ID + ".Price") >= bid && !topBidder.equalsIgnoreCase("None")) {
                                        player.sendMessage(Messages.BID_MORE_MONEY.getMessage());
                                        return;
                                    }

                                    Bukkit.getPluginManager().callEvent(new AuctionNewBidEvent(player, Methods.fromBase64(data.getString("Items." + ID + ".Item")), bid));
                                    data.set("Items." + ID + ".Price", bid);
                                    data.set("Items." + ID + ".TopBidder", player.getUniqueId().toString());

                                    Map<String, String> placeholders = new HashMap<>();
                                    placeholders.put("%Bid%", bid + "");

                                    player.sendMessage(Messages.BID_MESSAGE.getMessage(placeholders));

                                    Files.DATA.saveFile();

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
                                    if (item.getItemMeta().getDisplayName().equals(Methods.color(price))) {
                                        try {
                                            bidding.put(player.getUniqueId(), (bidding.get(player.getUniqueId()) + priceEdits.get(price)));
                                            inv.setItem(4, getBiddingItem(biddingID.get(player.getUniqueId())));
                                            inv.setItem(13, getBiddingGlass(player, biddingID.get(player.getUniqueId())));
                                            playClick(player);
                                            return;
                                        } catch (Exception ex) {
                                            player.closeInventory();
                                            player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage());
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (e.getView().getTitle().contains(Methods.color(config.getString("Settings.GUIName")))) {
                e.setCancelled(true);
                final int slot = e.getRawSlot();

                if (slot <= inv.getSize()) {
                    if (e.getCurrentItem() != null) {
                        final ItemStack item = e.getCurrentItem();

                        if (item.hasItemMeta()) {
                            if (item.getItemMeta().hasDisplayName()) {
                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.NextPage.Name")))) {
                                    Methods.updateAuction();
                                    int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);
                                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), page + 1);
                                    playClick(player);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.PreviousPage.Name")))) {
                                    Methods.updateAuction();
                                    int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);
                                    if (page == 1) page++;
                                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), page - 1);
                                    playClick(player);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Refesh.Name")))) {
                                    Methods.updateAuction();
                                    int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);
                                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), page);
                                    playClick(player);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bidding/Selling.Selling.Name")))) {
                                    openShop(player, ShopType.BID, shopCategory.get(player.getUniqueId()), 1);
                                    playClick(player);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bidding/Selling.Bidding.Name")))) {
                                    openShop(player, ShopType.SELL, shopCategory.get(player.getUniqueId()), 1);
                                    playClick(player);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cancelled/ExpiredItems.Name")))) {
                                    openPlayersExpiredList(player, 1);
                                    playClick(player);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.SellingItems.Name")))) {
                                    openPlayersCurrentList(player, 1);
                                    playClick(player);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Category1.Name")))) {
                                    openCategories(player, shopType.get(player.getUniqueId()));
                                    playClick(player);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Category2.Name")))) {
                                    openCategories(player, shopType.get(player.getUniqueId()));
                                    playClick(player);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Your-Item.Name")))) {
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Name")))) {
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Name")))) {
                                    return;
                                }
                            }

                            if (List.containsKey(player.getUniqueId())) {
                                if (List.get(player.getUniqueId()).size() >= slot) {
                                    int id = List.get(player.getUniqueId()).get(slot);
                                    boolean T = false;

                                    if (data.contains("Items")) {
                                        for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                                            int ID = data.getInt("Items." + i + ".StoreID");

                                            if (id == ID) {
                                                if (player.hasPermission("crazyauctions.admin") || player.hasPermission("crazyauctions.force-end")) {
                                                    if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                                        int num = 1;
                                                        for (; data.contains("OutOfTime/Cancelled." + num); num++) ;
                                                        String seller = data.getString("Items." + i + ".Seller");
                                                        Player sellerPlayer = Methods.getPlayer(seller);

                                                        if (Methods.isOnline(seller) && sellerPlayer != null) {
                                                            sellerPlayer.sendMessage(Messages.ADMIN_FORCE_CANCELLED_TO_PLAYER.getMessage());
                                                        }

                                                        AuctionCancelledEvent event = new AuctionCancelledEvent((sellerPlayer != null ? sellerPlayer : Methods.getOfflinePlayer(seller)), Methods.fromBase64(data.getString("Items." + ID + ".Item")), Reasons.ADMIN_FORCE_CANCEL);
                                                        Bukkit.getPluginManager().callEvent(event);
                                                        data.set("OutOfTime/Cancelled." + num + ".Seller", data.getString("Items." + i + ".Seller"));
                                                        data.set("OutOfTime/Cancelled." + num + ".Full-Time", data.getLong("Items." + i + ".Full-Time"));
                                                        data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
                                                        data.set("OutOfTime/Cancelled." + num + ".Item", data.getString("Items." + ID + ".Item"));
                                                        data.set("Items." + i, null);
                                                        Files.DATA.saveFile();
                                                        player.sendMessage(Messages.ADMIN_FORCE_CANCELLED.getMessage());
                                                        playClick(player);
                                                        int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);
                                                        openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), page);
                                                        return;
                                                    }
                                                }

                                                final Runnable runnable = () -> inv.setItem(slot, item);

                                                if (data.getString("Items." + i + ".Seller").equalsIgnoreCase(player.getUniqueId().toString())) {
                                                    String itemName = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Item");
                                                    String name = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Name");

                                                    ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                                                    if (config.contains("Settings.GUISettings.OtherSettings.Your-Item.Lore")) {
                                                        itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings.Your-Item.Lore"));
                                                    }

                                                    inv.setItem(slot, itemBuilder.build());
                                                    playClick(player);
                                                    player.getScheduler().runDelayed(plugin, task -> runnable.run(), null, 3 * 20);
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
                                                    player.getScheduler().runDelayed(plugin, task -> runnable.run(), null, 3 * 20);
                                                    return;
                                                }

                                                if (data.getBoolean("Items." + i + ".Biddable")) {
                                                    if (player.getUniqueId().toString().equalsIgnoreCase(data.getString("Items." + i + ".TopBidder"))) {
                                                        String itemName = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Item");
                                                        String name = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Name");

                                                        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                                                        if (config.contains("Settings.GUISettings.OtherSettings.Top-Bidder.Lore")) {
                                                            itemBuilder.setLore( config.getStringList("Settings.GUISettings.OtherSettings.Top-Bidder.Lore"));
                                                        }

                                                        inv.setItem(slot, itemBuilder.build());
                                                        playClick(player);
                                                        player.getScheduler().runDelayed(plugin, task -> runnable.run(), null, 3 * 20);
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

                                    if (!T) {
                                        playClick(player);
                                        openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);
                                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage());
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (e.getView().getTitle().contains(Methods.color(config.getString("Settings.Buying-Item")))) {
                e.setCancelled(true);
                int slot = e.getRawSlot();

                if (slot <= inv.getSize()) {
                    if (e.getCurrentItem() != null) {
                        ItemStack item = e.getCurrentItem();

                        if (item.hasItemMeta()) {
                            if (item.getItemMeta().hasDisplayName()) {
                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Confirm.Name")))) {
                                    String ID = IDs.get(player.getUniqueId());
                                    long cost = data.getLong("Items." + ID + ".Price");
                                    String seller = data.getString("Items." + ID + ".Seller");

                                    if (!data.contains("Items." + ID)) {
                                        playClick(player);
                                        openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);
                                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage());
                                        return;
                                    }

                                    if (Methods.isInvFull(player)) {
                                        playClick(player);
                                        player.closeInventory();
                                        player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                        return;
                                    }

                                    if (plugin.getSupport().getMoney(player) < cost) {
                                        playClick(player);
                                        player.closeInventory();
                                        HashMap<String, String> placeholders = new HashMap<>();
                                        placeholders.put("%Money_Needed%", (cost - plugin.getSupport().getMoney(player)) + "");
                                        placeholders.put("%money_needed%", (cost - plugin.getSupport().getMoney(player)) + "");
                                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
                                        return;
                                    }

                                    ItemStack i = Methods.fromBase64(data.getString("Items." + ID + ".Item"));

                                    plugin.getServer().getPluginManager().callEvent(new AuctionBuyEvent(player, i, cost));
                                    plugin.getSupport().removeMoney(player, cost);
                                    plugin.getSupport().addMoney(Methods.getOfflinePlayer(seller), cost);

                                    HashMap<String, String> placeholders = new HashMap<>();

                                    String price = Methods.getPrice(ID, false);

                                    placeholders.put("%Price%", price);
                                    placeholders.put("%price%", price);
                                    placeholders.put("%Player%", player.getName());
                                    placeholders.put("%player%", player.getName());

                                    player.sendMessage(Messages.BOUGHT_ITEM.getMessage(placeholders));

                                    if (seller != null && Methods.isOnline(seller) && Methods.getPlayer(seller) != null) {
                                        Player sell = Methods.getPlayer(seller);

                                        if (sell != null) {
                                            sell.sendMessage(Messages.PLAYER_BOUGHT_ITEM.getMessage(placeholders));
                                            playSoldSound(sell);
                                        }
                                    }

                                    player.getInventory().addItem(i);
                                    data.set("Items." + ID, null);
                                    Files.DATA.saveFile();
                                    playClick(player);
                                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cancel.Name")))) {
                                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);
                                    playClick(player);
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (e.getView().getTitle().contains(Methods.color(config.getString("Settings.Players-Current-Items")))) {
                e.setCancelled(true);
                int slot = e.getRawSlot();

                if (slot <= inv.getSize()) {
                    if (e.getCurrentItem() != null) {
                        ItemStack item = e.getCurrentItem();

                        if (item.hasItemMeta()) {
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
                                    boolean T = false;
                                    if (data.contains("Items")) {
                                        for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                                            int ID = data.getInt("Items." + i + ".StoreID");
                                            if (id == ID) {
                                                player.sendMessage(Messages.CANCELLED_ITEM.getMessage());
                                                AuctionCancelledEvent event = new AuctionCancelledEvent(player, Methods.fromBase64(data.getString("Items." + i + ".Item")), Reasons.PLAYER_FORCE_CANCEL);
                                                Bukkit.getPluginManager().callEvent(event);
                                                int num = 1;
                                                for (; data.contains("OutOfTime/Cancelled." + num); num++) ;
                                                data.set("OutOfTime/Cancelled." + num + ".Seller", data.getString("Items." + i + ".Seller"));
                                                data.set("OutOfTime/Cancelled." + num + ".Full-Time", data.getLong("Items." + i + ".Full-Time"));
                                                data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
                                                data.set("OutOfTime/Cancelled." + num + ".Item", data.getString("Items." + i + ".Item"));
                                                data.set("Items." + i, null);
                                                Files.DATA.saveFile();
                                                playClick(player);
                                                openPlayersCurrentList(player, 1);
                                                return;
                                            }
                                        }
                                    }

                                    if (!T) {
                                        playClick(player);
                                        openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);
                                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage());
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (e.getView().getTitle().contains(Methods.color(config.getString("Settings.Cancelled/Expired-Items")))) {
                e.setCancelled(true);
                final int slot = e.getRawSlot();
                if (slot <= inv.getSize()) {
                    if (e.getCurrentItem() != null) {
                        final ItemStack item = e.getCurrentItem();
                        if (item.hasItemMeta()) {
                            if (item.getItemMeta().hasDisplayName()) {
                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Back.Name")))) {
                                    Methods.updateAuction();
                                    playClick(player);
                                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.PreviousPage.Name")))) {
                                    Methods.updateAuction();
                                    int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);
                                    if (page == 1) page++;
                                    playClick(player);
                                    openPlayersExpiredList(player, (page - 1));
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Return.Name")))) {
                                    Methods.updateAuction();
                                    int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);
                                    if (data.contains("OutOfTime/Cancelled")) {
                                        for (String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                                            if (data.getString("OutOfTime/Cancelled." + i + ".Seller").equalsIgnoreCase(player.getUniqueId().toString())) {
                                                if (Methods.isInvFull(player)) {
                                                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                                    break;
                                                } else {
                                                    player.getInventory().addItem(Methods.fromBase64(data.getString("OutOfTime/Cancelled." + i + ".Item")));
                                                    data.set("OutOfTime/Cancelled." + i, null);
                                                }
                                            }
                                        }
                                    }

                                    player.sendMessage(Messages.GOT_ITEM_BACK.getMessage());
                                    Files.DATA.saveFile();
                                    playClick(player);
                                    openPlayersExpiredList(player, page);
                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.NextPage.Name")))) {
                                    Methods.updateAuction();
                                    int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);
                                    playClick(player);
                                    openPlayersExpiredList(player, (page + 1));
                                    return;
                                }
                            }

                            if (List.containsKey(player.getUniqueId())) {
                                if (List.get(player.getUniqueId()).size() >= slot) {
                                    int id = List.get(player.getUniqueId()).get(slot);
                                    boolean T = false;
                                    if (data.contains("OutOfTime/Cancelled")) {
                                        for (String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                                            int ID = data.getInt("OutOfTime/Cancelled." + i + ".StoreID");
                                            if (id == ID) {
                                                if (!Methods.isInvFull(player)) {
                                                    player.sendMessage(Messages.GOT_ITEM_BACK.getMessage());
                                                    player.getInventory().addItem(Methods.fromBase64(data.getString("OutOfTime/Cancelled." + i + ".Item")));
                                                    data.set("OutOfTime/Cancelled." + i, null);
                                                    Files.DATA.saveFile();
                                                    playClick(player);
                                                    openPlayersExpiredList(player, 1);
                                                } else {
                                                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                                }
                                                return;
                                            }
                                        }
                                    }

                                    if (!T) {
                                        playClick(player);
                                        openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);
                                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
