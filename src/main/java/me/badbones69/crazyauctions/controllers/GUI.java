package me.badbones69.crazyauctions.controllers;

import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.api.*;
import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.enums.CancelledReason;
import me.badbones69.crazyauctions.api.events.AuctionBuyEvent;
import me.badbones69.crazyauctions.api.events.AuctionCancelledEvent;
import me.badbones69.crazyauctions.api.events.AuctionNewBidEvent;
import me.badbones69.crazyauctions.currency.CurrencyManager;
import org.bukkit.Bukkit;
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
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

public class GUI implements Listener {
    
    private static HashMap<Player, Integer> bidding = new HashMap<>();
    private static HashMap<Player, String> biddingID = new HashMap<>();
    private static HashMap<Player, ShopType> shopType = new HashMap<>(); // Shop Type
    private static HashMap<Player, Category> shopCategory = new HashMap<>(); // Category Type
    private static HashMap<Player, List<Integer>> List = new HashMap<>();
    private static HashMap<Player, String> IDs = new HashMap<>();
    private static CrazyAuctions crazyAuctions = CrazyAuctions.getInstance();
    private static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyAuctions");
    
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
            shopCategory.put(player, cat);
        } else {
            shopCategory.put(player, Category.NONE);
        }
        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                List<String> lore = new ArrayList<>();
                if (data.getItemStack("Items." + i + ".Item") != null && (cat.getItems().contains(data.getItemStack("Items." + i + ".Item").getType()) || cat == Category.NONE)) {
                    if (data.getBoolean("Items." + i + ".Biddable")) {
                        if (sell == ShopType.BID) {
                            String seller = data.getString("Items." + i + ".Seller");
                            String topbidder = data.getString("Items." + i + ".TopBidder");
                            for (String l : config.getStringList("Settings.GUISettings.Bidding")) {
                                lore.add(l.replace("%TopBid%", Methods.getPrice(i, false)).replace("%topbid%", Methods.getPrice(i, false)).replace("%Seller%", seller).replace("%seller%", seller).replace("%TopBidder%", topbidder).replace("%topbidder%", topbidder).replace("%Time%", Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"))).replace("%time%", Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"))));
                            }
                            items.add(Methods.addLore(data.getItemStack("Items." + i + ".Item").clone(), lore));
                            ID.add(data.getInt("Items." + i + ".StoreID"));
                        }
                    } else {
                        if (sell == ShopType.SELL) {
                            for (String l : config.getStringList("Settings.GUISettings.SellingItemLore")) {
                                lore.add(l.replace("%Price%", String.format(Locale.ENGLISH, "%,d", Long.parseLong(Methods.getPrice(i, false)))).replace("%price%", String.format(Locale.ENGLISH, "%,d", Long.parseLong(Methods.getPrice(i, false)))).replace("%Seller%", data.getString("Items." + i + ".Seller")).replace("%seller%", data.getString("Items." + i + ".Seller")).replace("%Time%", Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"))).replace("%time%", Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"))));
                            }
                            items.add(Methods.addLore(data.getItemStack("Items." + i + ".Item").clone(), lore));
                            ID.add(data.getInt("Items." + i + ".StoreID"));
                        }
                    }
                }
            }
        }
        int maxPage = Methods.getMaxPage(items);
        for (; page > maxPage; page--) ;
        Inventory inv = Bukkit.createInventory(null, 54, Methods.color(config.getString("Settings.GUIName") + " #" + page));
        List<String> options = new ArrayList<>();
        options.add("SellingItems");
        options.add("Cancelled/ExpiredItems");
        options.add("PreviousPage");
        options.add("Refesh");
        options.add("NextPage");
        options.add("Category1");
        options.add("Category2");
        if (sell == ShopType.SELL) {
            shopType.put(player, ShopType.SELL);
            if (crazyAuctions.isBiddingEnabled()) {
                options.add("Bidding/Selling.Selling");
            }
            options.add("WhatIsThis.SellingShop");
        }
        if (sell == ShopType.BID) {
            shopType.put(player, ShopType.BID);
            if (crazyAuctions.isSellingEnabled()) {
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
            String cName = Methods.color(config.getString("Settings.GUISettings.Category-Settings." + shopCategory.get(player).getName() + ".Name"));
            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                for (String l : config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                    lore.add(l.replace("%Category%", cName).replace("%category%", cName));
                }
                inv.setItem(slot - 1, Methods.makeItem(id, 1, name, lore));
            } else {
                inv.setItem(slot - 1, Methods.makeItem(id, 1, name));
            }
        }
        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
        }
        List<Integer> Id = new ArrayList<>(Methods.getPageInts(ID, page));
        List.put(player, Id);
        player.openInventory(inv);
    }
    
    public static void openCategories(Player player, ShopType shop) {
        Methods.updateAuction();
        FileConfiguration config = Files.CONFIG.getFile();
        Inventory inv = Bukkit.createInventory(null, 54, Methods.color(config.getString("Settings.Categories")));
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
            if (config.contains("Settings.GUISettings." + o + ".Lore")) {
                inv.setItem(slot - 1, Methods.makeItem(id, 1, name, config.getStringList("Settings.GUISettings." + o + ".Lore")));
            } else {
                inv.setItem(slot - 1, Methods.makeItem(id, 1, name));
            }
        }
        shopType.put(player, shop);
        player.openInventory(inv);
    }
    
    public static void openPlayersCurrentList(Player player, int page) {
        Methods.updateAuction();
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        List<ItemStack> items = new ArrayList<>();
        List<Integer> ID = new ArrayList<>();
        Inventory inv = Bukkit.createInventory(null, 54, Methods.color(config.getString("Settings.Players-Current-Items")));
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
            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                inv.setItem(slot - 1, Methods.makeItem(id, 1, name, config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore")));
            } else {
                inv.setItem(slot - 1, Methods.makeItem(id, 1, name));
            }
        }
        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                if (UUID.fromString(data.getString("Items." + i + ".Seller")).equals(player.getUniqueId())) {
                    List<String> lore = new ArrayList<>();
                    for (String l : config.getStringList("Settings.GUISettings.CurrentLore")) {
                        lore.add(l.replace("%Price%", Methods.getPrice(i, false)).replace("%price%", Methods.getPrice(i, false)).replace("%Time%", Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"))).replace("%time%", Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"))));
                    }
                    items.add(Methods.addLore(data.getItemStack("Items." + i + ".Item").clone(), lore));
                    ID.add(data.getInt("Items." + i + ".StoreID"));
                }
            }
        }
        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
        }
        List<Integer> Id = new ArrayList<>(Methods.getPageInts(ID, page));
        List.put(player, Id);
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
                    if (UUID.fromString(data.getString("OutOfTime/Cancelled." + i + ".Seller")).equals(player.getUniqueId())) {
                        List<String> lore = new ArrayList<>();
                        for (String l : config.getStringList("Settings.GUISettings.Cancelled/ExpiredLore")) {
                            lore.add(l.replace("%Price%", Methods.getPrice(i, true)).replace("%price%", Methods.getPrice(i, true)).replace("%Time%", Methods.convertToTime(data.getLong("OutOfTime/Cancelled." + i + ".Full-Time"))).replace("%time%", Methods.convertToTime(data.getLong("OutOfTime/Cancelled." + i + ".Full-Time"))));
                        }
                        items.add(Methods.addLore(data.getItemStack("OutOfTime/Cancelled." + i + ".Item").clone(), lore));
                        ID.add(data.getInt("OutOfTime/Cancelled." + i + ".StoreID"));
                    }
                }
            }
        }
        int maxPage = Methods.getMaxPage(items);
        for (; page > maxPage; page--) ;
        Inventory inv = Bukkit.createInventory(null, 54, Methods.color(config.getString("Settings.Cancelled/Expired-Items") + " #" + page));
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
            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                inv.setItem(slot - 1, Methods.makeItem(id, 1, name, config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore")));
            } else {
                inv.setItem(slot - 1, Methods.makeItem(id, 1, name));
            }
        }
        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
        }
        List<Integer> Id = new ArrayList<>(Methods.getPageInts(ID, page));
        List.put(player, Id);
        player.openInventory(inv);
    }
    
    public static void openBuying(Player player, String ID) {
        Methods.updateAuction();
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        if (!data.contains("Items." + ID)) {
            openShop(player, ShopType.SELL, shopCategory.get(player), 1);
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
            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                item = Methods.makeItem(id, 1, name, config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore"));
            } else {
                item = Methods.makeItem(id, 1, name);
            }
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
        ItemStack item = data.getItemStack("Items." + ID + ".Item");
        List<String> lore = new ArrayList<>();
        for (String l : config.getStringList("Settings.GUISettings.SellingItemLore")) {
            lore.add(l.replace("%Price%", Methods.getPrice(ID, false)).replace("%price%", Methods.getPrice(ID, false)).replace("%Seller%", data.getString("Items." + ID + ".Seller")).replace("%seller%", data.getString("Items." + ID + ".Seller")).replace("%Time%", Methods.convertToTime(data.getLong("Items." + l + ".Time-Till-Expire"))).replace("%time%", Methods.convertToTime(data.getLong("Items." + l + ".Time-Till-Expire"))));
        }
        inv.setItem(4, Methods.addLore(item.clone(), lore));
        IDs.put(player, ID);
        player.openInventory(inv);
    }
    
    public static void openBidding(Player player, String ID) {
        Methods.updateAuction();
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        if (!data.contains("Items." + ID)) {
            openShop(player, ShopType.BID, shopCategory.get(player), 1);
            player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage());
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 27, Methods.color(config.getString("Settings.Bidding-On-Item")));
        if (!bidding.containsKey(player)) bidding.put(player, 0);
        if (Version.isNewer(Version.v1_12_R1)) {
            inv.setItem(9, Methods.makeItem("LIME_STAINED_GLASS_PANE", 1, "&a+1"));
            inv.setItem(10, Methods.makeItem("LIME_STAINED_GLASS_PANE", 1, "&a+10"));
            inv.setItem(11, Methods.makeItem("LIME_STAINED_GLASS_PANE", 1, "&a+100"));
            inv.setItem(12, Methods.makeItem("LIME_STAINED_GLASS_PANE", 1, "&a+1000"));
            inv.setItem(14, Methods.makeItem("RED_STAINED_GLASS_PANE", 1, "&c-1000"));
            inv.setItem(15, Methods.makeItem("RED_STAINED_GLASS_PANE", 1, "&c-100"));
            inv.setItem(16, Methods.makeItem("RED_STAINED_GLASS_PANE", 1, "&c-10"));
            inv.setItem(17, Methods.makeItem("RED_STAINED_GLASS_PANE", 1, "&c-1"));
        } else {
            inv.setItem(9, Methods.makeItem("160:5", 1, "&a+1"));
            inv.setItem(10, Methods.makeItem("160:5", 1, "&a+10"));
            inv.setItem(11, Methods.makeItem("160:5", 1, "&a+100"));
            inv.setItem(12, Methods.makeItem("160:5", 1, "&a+1000"));
            inv.setItem(14, Methods.makeItem("160:14", 1, "&c-1000"));
            inv.setItem(15, Methods.makeItem("160:14", 1, "&c-100"));
            inv.setItem(16, Methods.makeItem("160:14", 1, "&c-10"));
            inv.setItem(17, Methods.makeItem("160:14", 1, "&c-1"));
        }
        inv.setItem(13, getBiddingGlass(player, ID));
        inv.setItem(22, Methods.makeItem(config.getString("Settings.GUISettings.OtherSettings.Bid.Item"), 1, config.getString("Settings.GUISettings.OtherSettings.Bid.Name"), config.getStringList("Settings.GUISettings.OtherSettings.Bid.Lore")));
        
        inv.setItem(4, getBiddingItem(player, ID));
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
                    if (data.getBoolean("Items." + i + ".Biddable")) {
                        String seller = data.getString("Items." + i + ".Seller");
                        String topbidder = data.getString("Items." + i + ".TopBidder");
                        for (String l : config.getStringList("Settings.GUISettings.Bidding")) {
                            lore.add(l.replace("%TopBid%", Methods.getPrice(i, false)).replace("%topbid%", Methods.getPrice(i, false)).replace("%Seller%", seller).replace("%seller%", seller).replace("%TopBidder%", topbidder).replace("%topbidder%", topbidder).replace("%Time%", Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"))).replace("%time%", Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"))));
                        }
                    } else {
                        for (String l : config.getStringList("Settings.GUISettings.SellingItemLore")) {
                            lore.add(l.replace("%Price%", Methods.getPrice(i, false)).replace("%price%", Methods.getPrice(i, false)).replace("%Seller%", data.getString("Items." + i + ".Seller")).replace("%seller%", data.getString("Items." + i + ".Seller")).replace("%Time%", Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"))).replace("%time%", Methods.convertToTime(data.getLong("Items." + i + ".Time-Till-Expire"))));
                        }
                    }
                    items.add(Methods.addLore(data.getItemStack("Items." + i + ".Item").clone(), lore));
                    ID.add(data.getInt("Items." + i + ".StoreID"));
                }
            }
        }
        int maxPage = Methods.getMaxPage(items);
        for (; page > maxPage; page--) ;
        Inventory inv = Bukkit.createInventory(null, 54, Methods.color(config.getString("Settings.GUIName") + " #" + page));
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
            if (config.contains("Settings.GUISettings.OtherSettings." + o + ".Lore")) {
                inv.setItem(slot - 1, Methods.makeItem(id, 1, name, config.getStringList("Settings.GUISettings.OtherSettings." + o + ".Lore")));
            } else {
                inv.setItem(slot - 1, Methods.makeItem(id, 1, name));
            }
        }
        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
        }
        List.put(player, new ArrayList<>(Methods.getPageInts(ID, page)));
        player.openInventory(inv);
    }
    
    public static ItemStack getBiddingGlass(Player player, String ID) {
        FileConfiguration config = Files.CONFIG.getFile();
        String id = config.getString("Settings.GUISettings.OtherSettings.Bidding.Item");
        String name = config.getString("Settings.GUISettings.OtherSettings.Bidding.Name");
        ItemStack item;
        int bid = bidding.get(player);
        if (config.contains("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
            List<String> lore = new ArrayList<>();
            for (String l : config.getStringList("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
                lore.add(l.replace("%Bid%", bid + "").replace("%bid%", bid + "").replace("%TopBid%", Methods.getPrice(ID, false)).replace("%topbid%", Methods.getPrice(ID, false)));
            }
            item = Methods.makeItem(id, 1, name, lore);
        } else {
            item = Methods.makeItem(id, 1, name);
        }
        return item;
    }
    
    public static ItemStack getBiddingItem(Player player, String ID) {
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        String seller = data.getString("Items." + ID + ".Seller");
        String topbidder = data.getString("Items." + ID + ".TopBidder");
        ItemStack item = data.getItemStack("Items." + ID + ".Item");
        List<String> lore = new ArrayList<>();
        for (String l : config.getStringList("Settings.GUISettings.Bidding")) {
            lore.add(l.replace("%TopBid%", Methods.getPrice(ID, false)).replace("%topbid%", Methods.getPrice(ID, false)).replace("%Seller%", seller).replace("%seller%", seller).replace("%TopBidder%", topbidder).replace("%topbidder%", topbidder).replace("%Time%", Methods.convertToTime(data.getLong("Items." + ID + ".Time-Till-Expire"))).replace("%time%", Methods.convertToTime(data.getLong("Items." + ID + ".Time-Till-Expire"))));
        }
        return Methods.addLore(item.clone(), lore);
    }
    
    private static void playClick(Player player) {
        if (Files.CONFIG.getFile().contains("Settings.Sounds.Toggle")) {
            if (Files.CONFIG.getFile().getBoolean("Settings.Sounds.Toggle")) {
                String sound = Files.CONFIG.getFile().getString("Settings.Sounds.Sound");
                try {
                    player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 1);
                } catch (Exception e) {
                    if (Methods.getVersion() >= 191) {
                        player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
                    } else {
                        player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
                    }
                    Bukkit.getLogger().log(Level.WARNING, "[Crazy Auctions]>> You set the sound to " + sound + " and this is not a sound for your minecraft version. " + "Please go to the config and set a correct sound or turn the sound off in the toggle setting.");
                }
            }
        } else {
            if (Methods.getVersion() >= 191) {
                player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
            } else {
                player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
            }
        }
    }
    
    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        FileConfiguration config = Files.CONFIG.getFile();
        Inventory inv = e.getInventory();
        Player player = (Player) e.getPlayer();
        if (inv != null) {
            if (e.getView().getTitle().contains(Methods.color(config.getString("Settings.Bidding-On-Item")))) {
                bidding.remove(player);
            }
        }
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration data = Files.DATA.getFile();
        Player player = (Player) e.getWhoClicked();
        final Inventory inv = e.getInventory();
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
                                        openShop(player, shopType.get(player), cat, 1);
                                        playClick(player);
                                        return;
                                    }
                                    if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Back.Name")))) {
                                        openShop(player, shopType.get(player), shopCategory.get(player), 1);
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
                                    String ID = biddingID.get(player);
                                    int bid = bidding.get(player);
                                    String topBidder = data.getString("Items." + ID + ".TopBidder");
                                    if (CurrencyManager.getMoney(player) < bid) {
                                        HashMap<String, String> placeholders = new HashMap<>();
                                        placeholders.put("%Money_Needed%", (bid - CurrencyManager.getMoney(player)) + "");
                                        placeholders.put("%money_needed%", (bid - CurrencyManager.getMoney(player)) + "");
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
                                    Bukkit.getPluginManager().callEvent(new AuctionNewBidEvent(player, data.getItemStack("Items." + ID + ".Item"), bid));
                                    data.set("Items." + ID + ".Price", bid);
                                    data.set("Items." + ID + ".TopBidder", player.getUniqueId().toString());
                                    HashMap<String, String> placeholders = new HashMap<>();
                                    placeholders.put("%Bid%", bid + "");
                                    player.sendMessage(Messages.BID_MESSAGE.getMessage(placeholders));
                                    Files.DATA.saveFile();
                                    bidding.put(player, 0);
                                    player.closeInventory();
                                    playClick(player);
                                    return;
                                }
                                HashMap<String, Integer> priceEdits = new HashMap<>();
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
                                            bidding.put(player, (bidding.get(player) + priceEdits.get(price)));
                                            inv.setItem(4, getBiddingItem(player, biddingID.get(player)));
                                            inv.setItem(13, getBiddingGlass(player, biddingID.get(player)));
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
                                    openShop(player, shopType.get(player), shopCategory.get(player), page + 1);
                                    playClick(player);
                                    return;
                                }
                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.PreviousPage.Name")))) {
                                    Methods.updateAuction();
                                    int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);
                                    if (page == 1) page++;
                                    openShop(player, shopType.get(player), shopCategory.get(player), page - 1);
                                    playClick(player);
                                    return;
                                }
                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Refesh.Name")))) {
                                    Methods.updateAuction();
                                    int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);
                                    openShop(player, shopType.get(player), shopCategory.get(player), page);
                                    playClick(player);
                                    return;
                                }
                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bidding/Selling.Selling.Name")))) {
                                    openShop(player, ShopType.BID, shopCategory.get(player), 1);
                                    playClick(player);
                                    return;
                                }
                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bidding/Selling.Bidding.Name")))) {
                                    openShop(player, ShopType.SELL, shopCategory.get(player), 1);
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
                                    openCategories(player, shopType.get(player));
                                    playClick(player);
                                    return;
                                }
                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Category2.Name")))) {
                                    openCategories(player, shopType.get(player));
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
                            if (List.containsKey(player)) {
                                if (List.get(player).size() >= slot) {
                                    int id = List.get(player).get(slot);
                                    boolean T = false;
                                    if (data.contains("Items")) {
                                        for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                                            int ID = data.getInt("Items." + i + ".StoreID");
                                            if (id == ID) {
                                                if (player.hasPermission("crazyAuctions.admin") || player.hasPermission("crazyauctions.force-end")) {
                                                    if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                                        int num = 1;
                                                        for (; data.contains("OutOfTime/Cancelled." + num); num++) ;
                                                        String seller = data.getString("Items." + i + ".Seller");
                                                        Player sellerPlayer = Methods.getPlayer(seller);
                                                        if (Methods.isOnline(seller) && sellerPlayer != null) {
                                                            sellerPlayer.sendMessage(Messages.ADMIN_FORCE_CANCELLED_TO_PLAYER.getMessage());
                                                        }
                                                        AuctionCancelledEvent event = new AuctionCancelledEvent((sellerPlayer != null ? sellerPlayer : Bukkit.getOfflinePlayer(seller)), data.getItemStack("Items." + i + ".Item"), CancelledReason.ADMIN_FORCE_CANCEL);
                                                        Bukkit.getPluginManager().callEvent(event);
                                                        data.set("OutOfTime/Cancelled." + num + ".Seller", data.getString("Items." + i + ".Seller"));
                                                        data.set("OutOfTime/Cancelled." + num + ".Full-Time", data.getLong("Items." + i + ".Full-Time"));
                                                        data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
                                                        data.set("OutOfTime/Cancelled." + num + ".Item", data.getItemStack("Items." + i + ".Item"));
                                                        data.set("Items." + i, null);
                                                        Files.DATA.saveFile();
                                                        player.sendMessage(Messages.ADMIN_FORCE_CENCELLED.getMessage());
                                                        playClick(player);
                                                        int page = Integer.parseInt(e.getView().getTitle().split("#")[1]);
                                                        openShop(player, shopType.get(player), shopCategory.get(player), page);
                                                        return;
                                                    }
                                                }
                                                final Runnable runnable = () -> inv.setItem(slot, item);
                                                if (UUID.fromString(data.getString("Items." + i + ".Seller")).equals(player.getUniqueId())) {
                                                    String it = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Item");
                                                    String name = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Name");
                                                    ItemStack I;
                                                    if (config.contains("Settings.GUISettings.OtherSettings.Your-Item.Lore")) {
                                                        I = Methods.makeItem(it, 1, name, config.getStringList("Settings.GUISettings.OtherSettings.Your-Item.Lore"));
                                                    } else {
                                                        I = Methods.makeItem(it, 1, name);
                                                    }
                                                    inv.setItem(slot, I);
                                                    playClick(player);
                                                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, 3 * 20);
                                                    return;
                                                }
                                                long cost = data.getLong("Items." + i + ".Price");
                                                if (CurrencyManager.getMoney(player) < cost) {
                                                    String it = config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Item");
                                                    String name = config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Name");
                                                    ItemStack I;
                                                    if (config.contains("Settings.GUISettings.OtherSettings.Cant-Afford.Lore")) {
                                                        I = Methods.makeItem(it, 1, name, config.getStringList("Settings.GUISettings.OtherSettings.Cant-Afford.Lore"));
                                                    } else {
                                                        I = Methods.makeItem(it, 1, name);
                                                    }
                                                    inv.setItem(slot, I);
                                                    playClick(player);
                                                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, 3 * 20);
                                                    return;
                                                }
                                                if (data.getBoolean("Items." + i + ".Biddable")) {
                                                    if (player.getUniqueId().equals(UUID.fromString(data.getString("Items." + i + ".TopBidder")))) {
                                                        String it = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Item");
                                                        String name = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Name");
                                                        ItemStack I;
                                                        if (config.contains("Settings.GUISettings.OtherSettings.Top-Bidder.Lore")) {
                                                            I = Methods.makeItem(it, 1, name, config.getStringList("Settings.GUISettings.OtherSettings.Top-Bidder.Lore"));
                                                        } else {
                                                            I = Methods.makeItem(it, 1, name);
                                                        }
                                                        inv.setItem(slot, I);
                                                        playClick(player);
                                                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, 3 * 20);
                                                        return;
                                                    }
                                                    playClick(player);
                                                    openBidding(player, i);
                                                    biddingID.put(player, i);
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
                                        openShop(player, shopType.get(player), shopCategory.get(player), 1);
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
                                    String ID = IDs.get(player);
                                    long cost = data.getLong("Items." + ID + ".Price");
                                    String seller = data.getString("Items." + ID + ".Seller");
                                    if (!data.contains("Items." + ID)) {
                                        playClick(player);
                                        openShop(player, shopType.get(player), shopCategory.get(player), 1);
                                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage());
                                        return;
                                    }
                                    if (Methods.isInvFull(player)) {
                                        playClick(player);
                                        player.closeInventory();
                                        player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                        return;
                                    }
                                    if (CurrencyManager.getMoney(player) < cost) {
                                        playClick(player);
                                        player.closeInventory();
                                        HashMap<String, String> placeholders = new HashMap<>();
                                        placeholders.put("%Money_Needed%", (cost - CurrencyManager.getMoney(player)) + "");
                                        placeholders.put("%money_needed%", (cost - CurrencyManager.getMoney(player)) + "");
                                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
                                        return;
                                    }
                                    ItemStack i = data.getItemStack("Items." + ID + ".Item");
                                    Bukkit.getPluginManager().callEvent(new AuctionBuyEvent(player, i, cost));
                                    CurrencyManager.removeMoney(player, cost);
                                    CurrencyManager.addMoney(Methods.getOfflinePlayer(seller), cost);
                                    HashMap<String, String> placeholders = new HashMap<>();
                                    placeholders.put("%Price%", Methods.getPrice(ID, false));
                                    placeholders.put("%price%", Methods.getPrice(ID, false));
                                    placeholders.put("%Player%", player.getName());
                                    placeholders.put("%player%", player.getName());
                                    player.sendMessage(Messages.BOUGHT_ITEM.getMessage(placeholders));
                                    if (Methods.isOnline(seller) && Methods.getPlayer(seller) != null) {
                                        Player sell = Methods.getPlayer(seller);
                                        sell.sendMessage(Messages.PLAYER_BOUGHT_ITEM.getMessage(placeholders));
                                    }
                                    player.getInventory().addItem(i);
                                    data.set("Items." + ID, null);
                                    Files.DATA.saveFile();
                                    playClick(player);
                                    openShop(player, shopType.get(player), shopCategory.get(player), 1);
                                    return;
                                }
                                if (item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cancel.Name")))) {
                                    openShop(player, shopType.get(player), shopCategory.get(player), 1);
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
                                    openShop(player, shopType.get(player), shopCategory.get(player), 1);
                                    playClick(player);
                                    return;
                                }
                            }
                            if (List.containsKey(player)) {
                                if (List.get(player).size() >= slot) {
                                    int id = List.get(player).get(slot);
                                    boolean T = false;
                                    if (data.contains("Items")) {
                                        for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                                            int ID = data.getInt("Items." + i + ".StoreID");
                                            if (id == ID) {
                                                player.sendMessage(Messages.CANCELLED_ITEM.getMessage());
                                                AuctionCancelledEvent event = new AuctionCancelledEvent(player, data.getItemStack("Items." + i + ".Item"), CancelledReason.PLAYER_FORCE_CANCEL);
                                                Bukkit.getPluginManager().callEvent(event);
                                                int num = 1;
                                                for (; data.contains("OutOfTime/Cancelled." + num); num++) ;
                                                data.set("OutOfTime/Cancelled." + num + ".Seller", data.getString("Items." + i + ".Seller"));
                                                data.set("OutOfTime/Cancelled." + num + ".Full-Time", data.getLong("Items." + i + ".Full-Time"));
                                                data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
                                                data.set("OutOfTime/Cancelled." + num + ".Item", data.getItemStack("Items." + i + ".Item"));
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
                                        openShop(player, shopType.get(player), shopCategory.get(player), 1);
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
                                    openShop(player, shopType.get(player), shopCategory.get(player), 1);
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
                                            if (UUID.fromString(data.getString("OutOfTime/Cancelled." + i + ".Seller")).equals(player.getUniqueId())) {
                                                if (Methods.isInvFull(player)) {
                                                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                                    break;
                                                } else {
                                                    player.getInventory().addItem(data.getItemStack("OutOfTime/Cancelled." + i + ".Item"));
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
                            if (List.containsKey(player)) {
                                if (List.get(player).size() >= slot) {
                                    int id = List.get(player).get(slot);
                                    boolean T = false;
                                    if (data.contains("OutOfTime/Cancelled")) {
                                        for (String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                                            int ID = data.getInt("OutOfTime/Cancelled." + i + ".StoreID");
                                            if (id == ID) {
                                                if (!Methods.isInvFull(player)) {
                                                    player.sendMessage(Messages.GOT_ITEM_BACK.getMessage());
                                                    ItemStack IT = data.getItemStack("OutOfTime/Cancelled." + i + ".Item");
                                                    player.getInventory().addItem(IT);
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
                                        openShop(player, shopType.get(player), shopCategory.get(player), 1);
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
