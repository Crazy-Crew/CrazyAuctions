package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.Reasons;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.events.AuctionBuyEvent;
import com.badbones69.crazyauctions.api.events.AuctionCancelledEvent;
import com.badbones69.crazyauctions.api.events.AuctionNewBidEvent;
import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.api.guis.types.AuctionsMenu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiListener implements Listener {

    private static final CrazyAuctions plugin = CrazyAuctions.getPlugin();
    
    public static void openShop(Player player, ShopType shopType, Category category, int page) {
        FileConfiguration config = Files.config.getConfiguration();

        new AuctionsMenu(player, shopType, category, config.getString("Settings.GUIName", "N/A"), 54, page);
    }
    
    public static void openCategories(Player player, ShopType shop) {
        Methods.updateAuction();
        FileConfiguration config = Files.config.getConfiguration();

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

        HolderManager.addShopType(player, shop);
        player.openInventory(inv);
    }
    
    public static void openPlayersCurrentList(Player player, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

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
                    ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + i + ".Item"));

                    List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

                    lore.add(" ");

                    String price = Methods.getPrice(i, false);
                    String time = Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"));

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

        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
        }

        List<Integer> Id = new ArrayList<>(Methods.getPageInts(ID, page));

        HolderManager.addPages(player, Id);

        player.openInventory(inv);
    }
    
    public static void openPlayersExpiredList(Player player, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();

        if (data.contains("OutOfTime/Cancelled")) {
            for (String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                if (data.getString("OutOfTime/Cancelled." + i + ".Seller") != null) {
                    if (data.getString("OutOfTime/Cancelled." + i + ".Seller").equalsIgnoreCase(player.getUniqueId().toString())) {
                        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("OutOfTime/Cancelled." + i + ".Item"));

                        List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

                        lore.add(" ");

                        String price = Methods.getPrice(i, true);
                        String time = Methods.convertToTime(data.getLong("OutOfTime/Cancelled." + i + ".Full-Time"));

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

        HolderManager.addPages(player, Id);

        player.openInventory(inv);
    }
    
    public static void openBuying(Player player, String ID) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        if (!data.contains("Items." + ID)) {
            openShop(player, ShopType.SELL, HolderManager.getShopCategory(player), 1);

            player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

            return;
        }

        Inventory inv = plugin.getServer().createInventory(null, 9, Methods.color(config.getString("Settings.Buying-Item")));

        List<String> options = new ArrayList<>();

        options.add("Confirm");
        options.add("Cancel");

        for (String o : options) {
            String id = config.getString("Settings.GUISettings.OtherSettings." + o + ".Item");
            String name = config.getString("Settings.GUISettings.OtherSettings." + o + ".Name");
            ItemStack item;

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

            lore.add(" ");

            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                lore.addAll(config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore"));

                itemBuilder.setLore(lore);
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

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + ID + ".Item"));

        List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

        lore.add(" ");

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

        itemBuilder.setLore(lore);

        inv.setItem(4, itemBuilder.build());

        HolderManager.addId(player, ID);

        player.openInventory(inv);
    }
    
    public static void openBidding(Player player, String ID) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        if (!data.contains("Items." + ID)) {
            openShop(player, ShopType.BID, HolderManager.getShopCategory(player), 1);

            player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

            return;
        }

        Inventory inv = plugin.getServer().createInventory(null, 27, Methods.color(config.getString("Settings.Bidding-On-Item")));

        if (!HolderManager.containsBidding(player)) HolderManager.addBidding(player, 0);

        final ItemBuilder builder = new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setAmount(1);

        inv.setItem(9, builder.setName("&a+1").build());
        inv.setItem(10, builder.setName("&a+10").build());
        inv.setItem(11, builder.setName("&a+100").build());
        inv.setItem(12, builder.setName("&a+1000").build());
        inv.setItem(14, builder.setName("&c-1000").build());
        inv.setItem(15, builder.setName("&c-100").build());
        inv.setItem(16, builder.setName("&c-10").build());
        inv.setItem(17, builder.setName("&c-1").build());
        inv.setItem(13, getBiddingGlass(player, ID));

        inv.setItem(22, new ItemBuilder().setMaterial(config.getString("Settings.GUISettings.OtherSettings.Bid.Item")).setAmount(1)
                .setName(config.getString("Settings.GUISettings.OtherSettings.Bid.Name")).setLore(config.getStringList("Settings.GUISettings.OtherSettings.Bid.Lore")).build());

        inv.setItem(4, getBiddingItem(ID));

        player.openInventory(inv);
    }

    public static void openViewer(Player player, String other, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();

        if (!data.contains("Items")) {
            data.set("Items.Clear", null);

            Files.data.save();
        }

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                if (data.getString("Items." + i + ".Seller").equalsIgnoreCase(other)) {
                    ItemBuilder itemBuilder = ItemBuilder.convertItemStack(data.getString("Items." + ID + ".Item"));

                    List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

                    lore.add(" ");

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

        HolderManager.addPages(player, Methods.getPageInts(ID, page));

        player.openInventory(inv);
    }
    
    private static ItemStack getBiddingGlass(Player player, String ID) {
        FileConfiguration config = Files.config.getConfiguration();

        String id = config.getString("Settings.GUISettings.OtherSettings.Bidding.Item");
        String name = config.getString("Settings.GUISettings.OtherSettings.Bidding.Name");

        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

        int bid = HolderManager.getBidding(player);

        String price = Methods.getPrice(ID, false);

        if (config.contains("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
            List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

            lore.add(" ");

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
        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        ItemStack item = Methods.fromBase64(data.getString("Items." + ID + ".Item"));

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(item);

        List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

        lore.add(" ");

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

        itemBuilder.setLore(lore);

        return itemBuilder.build();
    }

    private void playSoldSound(@NotNull Player player) {
        FileConfiguration config = Files.config.getConfiguration();

        String sound = config.getString("Settings.Sold-Item-Sound", "");

        if (sound.isEmpty()) return;

        try {
            player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 1);
        } catch (Exception ignored) {}
    }
    
    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        FileConfiguration config = Files.config.getConfiguration();

        Player player = (Player) e.getPlayer();

        if (e.getView().getTitle().contains(Methods.color(config.getString("Settings.Bidding-On-Item")))) HolderManager.removeBidding(player);
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

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
                                        openShop(player, HolderManager.getShopType(player), cat, 1);

                                        playClick(player);

                                        return;
                                    }

                                    if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Back.Name")))) {
                                        openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

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
                                    String ID = HolderManager.getBidId(player);
                                    int bid = HolderManager.getBidding(player);
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

                                    plugin.getServer().getPluginManager().callEvent(new AuctionNewBidEvent(player, Methods.fromBase64(data.getString("Items." + ID + ".Item")), bid));

                                    data.set("Items." + ID + ".Price", bid);
                                    data.set("Items." + ID + ".TopBidder", player.getUniqueId().toString());

                                    Map<String, String> placeholders = new HashMap<>();
                                    placeholders.put("%Bid%", bid + "");

                                    player.sendMessage(Messages.BID_MESSAGE.getMessage(player, placeholders));

                                    Files.data.save();

                                    HolderManager.addBidding(player, 0);
                                    player.closeInventory();
                                    //playClick(player);
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
                                            HolderManager.addBidding(player, HolderManager.getBidding(player) + priceEdits.get(price));

                                            inv.setItem(4, getBiddingItem(HolderManager.getBidId(player)));

                                            inv.setItem(13, getBiddingGlass(player, HolderManager.getBidId(player)));

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
                                    String ID = HolderManager.getId(player);
                                    long cost = data.getLong("Items." + ID + ".Price");
                                    String seller = data.getString("Items." + ID + ".Seller");

                                    if (!data.contains("Items." + ID)) {
                                        playClick(player);

                                        openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

                                        return;
                                    }

                                    if (Methods.isInvFull(player)) {
                                        playClick(player);

                                        player.closeInventory();
                                        player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

                                        return;
                                    }

                                    if (plugin.getSupport().getMoney(player) < cost) {
                                        playClick(player);
                                        player.closeInventory();

                                        Map<String, String> placeholders = new HashMap<>();
                                        placeholders.put("%Money_Needed%", (cost - plugin.getSupport().getMoney(player)) + "");
                                        placeholders.put("%money_needed%", (cost - plugin.getSupport().getMoney(player)) + "");

                                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                                        return;
                                    }

                                    ItemStack i = Methods.fromBase64(data.getString("Items." + ID + ".Item"));

                                    plugin.getServer().getPluginManager().callEvent(new AuctionBuyEvent(player, i, cost));
                                    plugin.getSupport().removeMoney(player, cost);
                                    plugin.getSupport().addMoney(Methods.getOfflinePlayer(seller), cost);

                                    Map<String, String> placeholders = new HashMap<>();

                                    String price = Methods.getPrice(ID, false);

                                    placeholders.put("%Price%", price);
                                    placeholders.put("%price%", price);
                                    placeholders.put("%Player%", player.getName());
                                    placeholders.put("%player%", player.getName());

                                    player.sendMessage(Messages.BOUGHT_ITEM.getMessage(player, placeholders));

                                    if (seller != null && Methods.isOnline(seller) && Methods.getPlayer(seller) != null) {
                                        Player sell = Methods.getPlayer(seller);

                                        if (sell != null) {
                                            sell.sendMessage(Messages.PLAYER_BOUGHT_ITEM.getMessage(player, placeholders));
                                            playSoldSound(sell);
                                        }
                                    }

                                    player.getInventory().addItem(i);

                                    data.set("Items." + ID, null);
                                    Files.data.save();

                                    playClick(player);

                                    openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                                    return;
                                }

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cancel.Name")))) {
                                    openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

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
                                    openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                                    playClick(player);

                                    return;
                                }
                            }

                            if (HolderManager.containsPage(player)) {
                                final List<Integer> pages = HolderManager.getPages(player);

                                if (pages.size() >= slot) {
                                    int id = pages.get(slot);
                                    boolean T = false;
                                    if (data.contains("Items")) {
                                        for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                                            int ID = data.getInt("Items." + i + ".StoreID");
                                            if (id == ID) {
                                                player.sendMessage(Messages.CANCELLED_ITEM.getMessage(player));

                                                AuctionCancelledEvent event = new AuctionCancelledEvent(player, Methods.fromBase64(data.getString("Items." + i + ".Item")), Reasons.PLAYER_FORCE_CANCEL);
                                                plugin.getServer().getPluginManager().callEvent(event);

                                                int num = 1;
                                                for (; data.contains("OutOfTime/Cancelled." + num); num++) ;

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

                                    if (!T) {
                                        playClick(player);

                                        openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

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

                                    openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

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

                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.NextPage.Name")))) {
                                    Methods.updateAuction();

                                    int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);

                                    playClick(player);

                                    openPlayersExpiredList(player, (page + 1));

                                    return;
                                }
                            }

                            if (HolderManager.containsPage(player)) {
                                final List<Integer> pages = HolderManager.getPages(player);

                                if (pages.size() >= slot) {
                                    int id = pages.get(slot);

                                    boolean T = false;

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

                                    if (!T) {
                                        playClick(player);

                                        openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void playClick(Player player) {
        FileConfiguration config = Files.config.getConfiguration();

        if (config.getBoolean("Settings.Sounds.Toggle", false)) {
            String sound = config.getString("Settings.Sounds.Sound");

            try {
                player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 1);
            } catch (Exception e) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1F);
            }
        }
    }
}