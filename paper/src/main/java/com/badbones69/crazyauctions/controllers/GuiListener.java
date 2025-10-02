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
import com.badbones69.crazyauctions.api.enums.CurrencyAuctionSession;
import com.badbones69.crazyauctions.api.enums.CurrencyData;
import com.badbones69.crazyauctions.api.events.AuctionBuyEvent;
import com.badbones69.crazyauctions.api.events.AuctionNewBidEvent;
import com.badbones69.crazyauctions.currency.EconomySession;
import com.badbones69.crazyauctions.currency.EconomySessionFactory;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;

public class GuiListener implements Listener {

    private static final CrazyAuctions plugin = CrazyAuctions.get();
    private static final CrazyManager crazyManager = plugin.getCrazyManager();

    private static final Map<UUID, Double> bidding = new HashMap<>();
    private static final Map<UUID, String> biddingID = new HashMap<>();
    private static final Map<UUID, ShopType> shopType = new HashMap<>(); // Shop Type
    private static final Map<UUID, Category> shopCategory = new HashMap<>(); // Category Type
    private static final Map<UUID, List<Integer>> playerPageItemsId = new HashMap<>();
    private static final Map<UUID, String> playerBuyingId = new HashMap<>();
    private static final Map<UUID, CurrencyAuctionSession> currencySessions = new HashMap<>();

    public static void openShop(@NotNull Player player, @NotNull ShopType shop, @NotNull Category cat, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();
        List<ItemStack> items = new ArrayList<>();
        List<Integer> itemsId = new ArrayList<>();

        shopCategory.put(player.getUniqueId(), cat);

        for (ConfigurationSection itemSection : crazyManager.getItems(shop)) {
            ItemBuilder itemBuilder = ItemBuilder.convertItemStack(itemSection.getString("Item"));
            if (cat == Category.NONE || cat.getItems().contains(itemBuilder.getItemStack().getType())) {
                items.add(buildAuctionDisplay(itemSection));
                itemsId.add(itemSection.getInt("StoreID"));
            }
        }

        List<String> options = new ArrayList<>(){{
            add("OtherSettings.SellingItems");
            add("OtherSettings.Cancelled/ExpiredItems");
            add("OtherSettings.PreviousPage");
            add("OtherSettings.Refresh");
            add("OtherSettings.NextPage");
            add("OtherSettings.Category1");
            add("OtherSettings.Category2");
        }};

        if (shop == ShopType.SELL) {
            shopType.put(player.getUniqueId(), ShopType.SELL);

            if (crazyManager.isBiddingEnabled()) {
                options.add("OtherSettings.Bidding/Selling.Selling");
            }

            options.add("OtherSettings.WhatIsThis.SellingShop");
        }

        if (shop == ShopType.BID) {
            shopType.put(player.getUniqueId(), ShopType.BID);

            if (crazyManager.isSellingEnabled()) {
                options.add("OtherSettings.Bidding/Selling.Bidding");
            }

            options.add("OtherSettings.WhatIsThis.BiddingShop");
        }

        page = Math.min(Methods.getMaxPage(items), page);

        Inventory inv = new AuctionMenu(54, Methods.color(config.getString("Settings.GUIName") + " #" + page), page).getInventory();

        String categoryName = Methods.color(config.getString("Settings.GUISettings.Category-Settings." + shopCategory.get(player.getUniqueId()).getName() + ".Name"));
        setOptions(options, config, inv, categoryName);

        setPage(inv, page, items, itemsId, player);
    }

    private static void setPage(Inventory inv, int page, List<ItemStack> items, List<Integer> itemsId, Player player) {
        for (ItemStack item : Methods.getPage(items, page)) {
            int slot = inv.firstEmpty();
            inv.setItem(slot, item);
        }
        List<Integer> pageItemsId = new ArrayList<>(Methods.getPageInts(itemsId, page));
        playerPageItemsId.put(player.getUniqueId(), pageItemsId);

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

        setOptions(options, config, inv);

        shopType.put(player.getUniqueId(), shop);
        player.openInventory(inv);
    }

    public static void openPlayersCurrentList(@NotNull Player player, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();

        List<ItemStack> items = new ArrayList<>();
        List<Integer> itemsId = new ArrayList<>();
        
        for (ConfigurationSection itemSection : crazyManager.getPlayerItems(player.getUniqueId().toString())) {
            String price = crazyManager.getPriceWithCurrency(Methods.getPrice(itemSection), Methods.getCurrency(itemSection));
            String time = Methods.convertToTime(itemSection.getLong("Time-Till-Expire"));

            ItemBuilder itemBuilder = ItemBuilder.convertItemStack(itemSection.getString("Item"));

            List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

            for (String l : config.getStringList("Settings.GUISettings.CurrentLore")) {
                lore.add(l.replace("%Price%", price)
                        .replace("%price%", price)
                        .replace("%Time%", time)
                        .replace("%time%", time));
            }

            itemBuilder.setLore(lore);

            items.add(itemBuilder.build());

            itemsId.add(itemSection.getInt("StoreID"));
        }

        Inventory inv = new AuctionMenu(54, Methods.color(config.getString("Settings.Players-Current-Items"))).getInventory();

        List<String> options = new ArrayList<>(){{
            add("OtherSettings.Back");
            add("OtherSettings.WhatIsThis.CurrentItems");
        }};
        setOptions(options, config, inv);

        setPage(inv, page, items, itemsId, player);
    }

    public static void openPlayersExpiredList(@NotNull Player player, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();

        List<ItemStack> items = new ArrayList<>();
        List<Integer> itemsId = new ArrayList<>();

        for (ConfigurationSection itemSection : crazyManager.getExpiredItems(player.getUniqueId().toString())) {
            String price = crazyManager.getPriceWithCurrency(Methods.getPrice(itemSection), Methods.getCurrency(itemSection));
            String time = Methods.convertToTime(itemSection.getLong("Full-Time"));

            ItemBuilder itemBuilder = ItemBuilder.convertItemStack(itemSection.getString("Item"));
            List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());
            for (String l : config.getStringList("Settings.GUISettings.Cancelled/ExpiredLore")) {
                lore.add(l.replace("%Price%", price)
                        .replace("%price%", price)
                        .replace("%Time%", time)
                        .replace("%time%", time));
            }

            itemBuilder.setLore(lore);
            items.add(itemBuilder.build());

            itemsId.add(itemSection.getInt("StoreID"));
        }

        page = Math.min(Methods.getMaxPage(items), page);

        Inventory inv = new AuctionMenu(54, Methods.color(config.getString("Settings.Cancelled/Expired-Items") + " #" + page), page).getInventory();

        List<String> options = new ArrayList<>(){{
            add("OtherSettings.Back");
            add("OtherSettings.PreviousPage");
            add("OtherSettings.Return");
            add("OtherSettings.NextPage");
            add("OtherSettings.WhatIsThis.Cancelled/ExpiredItems");
        }};

        setOptions(options, config, inv);

        setPage(inv, page, items, itemsId, player);
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

        ConfigurationSection itemSection = data.getConfigurationSection("Items." + ID);
        inv.setItem(4, buildAuctionDisplay(itemSection));
        playerBuyingId.put(player.getUniqueId(), ID);

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

        ConfigurationSection itemSection = data.getConfigurationSection("Items." + ID);
        inv.setItem(4, buildAuctionDisplay(itemSection));

        if (!bidding.containsKey(player.getUniqueId())) {
            bidding.put(player.getUniqueId(), Double.parseDouble(Methods.getPrice(itemSection)));
        };

        inv.setItem(9, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&a+1").setAmount(1).build());
        inv.setItem(10, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&a+10").setAmount(1).build());
        inv.setItem(11, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&a+100").setAmount(1).build());
        inv.setItem(12, new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName("&a+1000").setAmount(1).build());
        inv.setItem(14, new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).setName("&c-1000").setAmount(1).build());
        inv.setItem(15, new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).setName("&c-100").setAmount(1).build());
        inv.setItem(16, new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).setName("&c-10").setAmount(1).build());
        inv.setItem(17, new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).setName("&c-1").setAmount(1).build());
        inv.setItem(13, getBiddingGlass(player, itemSection));

        inv.setItem(22, new ItemBuilder().setMaterial(config.getString("Settings.GUISettings.OtherSettings.Bid.Item")).setAmount(1)
                .setName(config.getString("Settings.GUISettings.OtherSettings.Bid.Name")).setLore(config.getStringList("Settings.GUISettings.OtherSettings.Bid.Lore")).build());

        player.openInventory(inv);
    }

    public static void openViewer(@NotNull Player player, @NotNull String other, int page) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        List<ItemStack> items = new ArrayList<>();
        List<Integer> itemsId = new ArrayList<>();

        if (!Methods.isUUID(other)) other = String.valueOf(plugin.getServer().getPlayerUniqueId(other));
        
        for (ConfigurationSection itemSection : crazyManager.getPlayerItems(other)) {
            items.add(buildAuctionDisplay(itemSection));
            itemsId.add(itemSection.getInt("StoreID"));
        }

        page = Math.min(Methods.getMaxPage(items), page);
        Inventory inv = new AuctionMenu(54, Methods.color(config.getString("Settings.GUIName") + " #" + page), page).getInventory();

        List<String> options = new ArrayList<>();
        options.add("OtherSettings.WhatIsThis.Viewing");
        setOptions(options, config, inv);

        setPage(inv, page, items, itemsId, player);
    }

    public static void openCurrency(Player player, ItemStack item, int amount, double price, boolean isBid) {
        FileConfiguration config = Files.config.getConfiguration();
        String title = config.getString("Settings.GUISettings.Currency.Title");
        Inventory inv = new AuctionMenu(9, Methods.color(title)).getInventory();

        NamespacedKey currencyKey = new NamespacedKey(plugin, "currency");

        Map<String, CurrencyData> currencies = crazyManager.getRegisteredCurrencies();
        int slot = 0;

        for (Map.Entry<String, CurrencyData> entry : currencies.entrySet()) {
            if (slot >= 9) break;

            CurrencyData currency = entry.getValue();
            ItemBuilder itemBuilder = ItemBuilder.convertItemStack(currency.getIcon());
            ItemMeta meta = itemBuilder.getItemMeta();

            meta.getPersistentDataContainer().set(currencyKey, PersistentDataType.STRING, currency.getId());

            List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());
            for (String l : config.getStringList("Settings.GUISettings.Currency.Lore")) {
                lore.add(l.replace("%Currency%", currency.getName())
                        .replace("%currency%", currency.getName())
                        .replace("%Price%", String.valueOf(price) + " " + currency.getSymbol())
                        .replace("%price%", String.valueOf(price) + " " + currency.getSymbol()));
            }

            itemBuilder.setName(Methods.color("§e" + currency.getName()));
            itemBuilder.setLore(lore);
            itemBuilder.setItemMeta(meta);

            inv.setItem(slot, itemBuilder.build());
            slot++;
        }

        // Save temporary data (so that we know later what the player is selling)
        currencySessions.put(player.getUniqueId(), new CurrencyAuctionSession(item.clone(), amount, price, isBid));
        player.openInventory(inv);
    }

    private static void setOptions(@NotNull List<String> options, @NotNull FileConfiguration config, @NotNull Inventory inv, @NotNull String categoryName) {
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
                if (categoryName.isEmpty()) {
                    itemBuilder.setLore(config.getStringList("Settings.GUISettings." + option + ".Lore"));
                } else {
                    // support openShop
                    List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());
                    if (config.contains("Settings.GUISettings." + option + ".Lore")) {
                        for (String l : config.getStringList("Settings.GUISettings." + option + ".Lore")) {
                            lore.add(l.replace("%Category%", categoryName).replace("%category%", categoryName));
                        }
                    }
                    itemBuilder.setLore(lore);
                }
            }

            inv.setItem(slot - 1, itemBuilder.build());
        }
    }

    private static void setOptions(@NotNull List<String> options, @NotNull FileConfiguration config, @NotNull Inventory inv) {
        setOptions(options, config, inv, "");
    }

    private static ItemStack getBiddingGlass(@NotNull Player player, @NotNull ConfigurationSection itemSection) {
        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        String id = config.getString("Settings.GUISettings.OtherSettings.Bidding.Item");
        String name = config.getString("Settings.GUISettings.OtherSettings.Bidding.Name");

        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

        String bid = crazyManager.getPriceWithCurrency(String.valueOf(bidding.get(player.getUniqueId())), Methods.getCurrency(itemSection));
        String price = crazyManager.getPriceWithCurrency(Methods.getPrice(itemSection), Methods.getCurrency(itemSection));

        if (config.contains("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
            List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

            for (String l : config.getStringList("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
                lore.add(l.replace("%Bid%", bid)
                        .replace("%bid%", bid)
                        .replace("%TopBid%", price)
                        .replace("%topbid%", price));
            }

            itemBuilder.setLore(lore);
        }

        return itemBuilder.build();
    }

    private static ItemStack buildAuctionDisplay(@NotNull ConfigurationSection itemSection) {
        FileConfiguration config = Files.config.getConfiguration();

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(itemSection.getString("Item"));
        String sellerName = itemSection.getString("SellerName", "N/A");
        String price = crazyManager.getPriceWithCurrency(Methods.getPrice(itemSection), Methods.getCurrency(itemSection));
        String time = Methods.convertToTime(itemSection.getLong("Time-Till-Expire"));

        List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());
        if (itemSection.getBoolean("Biddable")) {
            String topBidderName = itemSection.getString("TopBidderName", "N/A");
            for (String l : config.getStringList("Settings.GUISettings.Bidding")) {
                lore.add(l.replace("%TopBid%", price)
                          .replace("%topbid%", price)
                          .replace("%Seller%", sellerName)
                          .replace("%seller%", sellerName)
                          .replace("%TopBidder%", topBidderName)
                          .replace("%topbidder%", topBidderName)
                          .replace("%Time%", time)
                          .replace("%time%", time));
            }
        } else {
            for (String l : config.getStringList("Settings.GUISettings.SellingItemLore")) {
                lore.add(l.replace("%Price%", price)
                          .replace("%price%", price)
                          .replace("%Seller%", sellerName)
                          .replace("%seller%", sellerName)
                          .replace("%Time%", time)
                          .replace("%time%", time));
            }
        }
        itemBuilder.setLore(lore);
        return itemBuilder.build();
    }

    private static void playClick(@NotNull Player player) {
        FileConfiguration config = Files.config.getConfiguration();
        String soundName = config.getString("Settings.Sounds.Sound", "UI_BUTTON_CLICK");
        playSoldSound(player, soundName);
    }

    private static void playSoldSound(@NotNull Player player, String soundName) {
         FileConfiguration config = Files.config.getConfiguration();
        if (config.getBoolean("Settings.Sounds.Toggle", false)) {
            if (soundName.isEmpty()) return;

            String enumName = soundName.toUpperCase().replace(" ", "_");

            try {
                Sound sound = Sound.valueOf(enumName);
                player.playSound(player.getLocation(), sound, 1, 1);
            } catch (IllegalArgumentException e) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                plugin.getLogger().warning("Incorrect sound in Settings.Sold-Item-Sound:'" + soundName);
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof  AuctionMenu auctionMenu)) return;
        FileConfiguration config = Files.config.getConfiguration();

        Player player = (Player) event.getPlayer();

        if (auctionMenu.getTitle().contains(Methods.color(config.getString("Settings.Bidding-On-Item")))) {
            bidding.remove(player);
            biddingID.remove(player);
        }

        if (auctionMenu.getTitle().contains(config.getString("Settings.Buying-Item"))) {
            playerBuyingId.remove(player.getUniqueId());
        }

        if (auctionMenu.getTitle().contains(config.getString("Settings.GUISettings.Currency.Title"))) {
            if (currencySessions.containsKey(player.getUniqueId())){
                currencySessions.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent clickEvent) {
        if (!(clickEvent.getInventory().getHolder() instanceof  AuctionMenu auctionMenu)) return;
        clickEvent.setCancelled(true);

        Player player = (Player) clickEvent.getWhoClicked();
        ItemStack item = clickEvent.getCurrentItem();
        Inventory inv = clickEvent.getClickedInventory();
        int slot = clickEvent.getRawSlot();

        if (item == null || !item.hasItemMeta() || inv == null || slot >= inv.getSize()) return;

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

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
                    ConfigurationSection itemSection = data.getConfigurationSection("Items." + ID);
                    double bid = bidding.get(player.getUniqueId());
                    String topBidder = itemSection.getString("TopBidder");
                    String currency = Methods.getCurrency(itemSection);

                    EconomySession session = crazyManager.getEconomySession(currency);

                    if (session.getMoney(player) < bid) {
                        String MoneyNeeded = crazyManager.getPriceWithCurrency(String.valueOf(bid - session.getMoney(player)), currency);

                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Money_Needed%", MoneyNeeded);
                        placeholders.put("%money_needed%", MoneyNeeded);

                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                        return;
                    }

                    if (itemSection.getDouble("Price") > bid) {
                        player.sendMessage(Messages.BID_MORE_MONEY.getMessage(player));

                        return;
                    }

                    if (itemSection.getDouble("Price") >= bid && topBidder != null) {
                        player.sendMessage(Messages.BID_MORE_MONEY.getMessage(player));

                        return;
                    }

                    new AuctionNewBidEvent(player, Methods.fromBase64(itemSection.getString("Item")), bid, currency).callEvent();

                    itemSection.set("Price", bid);
                    itemSection.set("TopBidder", player.getUniqueId().toString());
                    itemSection.set("TopBidderName", player.getName());

                    Map<String, String> placeholders = new HashMap<>();
                    String bidWithCurrency = crazyManager.getPriceWithCurrency(String.valueOf(bid), currency);
                    placeholders.put("%Bid%", bidWithCurrency);
                    placeholders.put("%bid%", bidWithCurrency);

                    player.sendMessage(Messages.BID_MESSAGE.getMessage(player, placeholders));

                    Files.data.save();

                    bidding.put(player.getUniqueId(), (double) 0);
                    player.closeInventory();
                    playClick(player);
                    return;
                }

                Map<String, Integer> priceEdits = new HashMap<>(){{
                    put("&a+1", 1);
                    put("&a+10", 10);
                    put("&a+100", 100);
                    put("&a+1000", 1000);
                    put("&c-1", -1);
                    put("&c-10", -10);
                    put("&c-100", -100);
                    put("&c-1000", -1000);
                }};

                for (String price : priceEdits.keySet()) {
                    if (displayName.equals(Methods.color(price))) {
                        try {
                            bidding.put(player.getUniqueId(), (bidding.get(player.getUniqueId()) + priceEdits.get(price)));

                            ConfigurationSection itemSection = data.getConfigurationSection("Items." + biddingID.get(player.getUniqueId()));

                            inv.setItem(4, buildAuctionDisplay(itemSection));

                            inv.setItem(13, getBiddingGlass(player, itemSection));

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

                int pageNumber = auctionMenu.getPageNumber();

                String displayName = item.getItemMeta().getDisplayName();

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.NextPage.Name")))) {
                    Methods.updateAuction();

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), pageNumber + 1);

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.PreviousPage.Name")))) {
                    Methods.updateAuction();

                    if (pageNumber == 1) pageNumber++;

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), pageNumber - 1);

                    playClick(player);

                    return;
                }

                if (displayName.equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Refresh.Name")))) {
                    Methods.updateAuction();

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), pageNumber);

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

            if (playerPageItemsId.containsKey(player.getUniqueId())) {
                if (playerPageItemsId.get(player.getUniqueId()).size() >= slot) {
                    int id = playerPageItemsId.get(player.getUniqueId()).get(slot);

                    for (ConfigurationSection itemSection : crazyManager.getItems()) {
                        if (id != itemSection.getInt("StoreID")) {
                            continue;
                        }

                        String itemId = itemSection.getName();

                        if (player.hasPermission("crazyauctions.admin") || player.hasPermission("crazyauctions.force-end")) {
                            if (clickEvent.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

                                OfflinePlayer seller = Methods.getOfflinePlayer(itemSection.getString("Seller"));

                                if (seller.getPlayer() != null) {
                                    seller.getPlayer().sendMessage(Messages.ADMIN_FORCE_CANCELLED_TO_PLAYER.getMessage(player));
                                }

                                Methods.expireItem(1, seller, itemSection, data, Reasons.ADMIN_FORCE_CANCEL);

                                Files.data.save();

                                player.sendMessage(Messages.ADMIN_FORCE_CANCELLED.getMessage(player));

                                playClick(player);

                                openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), auctionMenu.getPageNumber());

                                return;
                            }
                        }

                        if (Objects.equals(itemSection.getString("Seller"), player.getUniqueId().toString())) {
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

                        double cost = itemSection.getDouble("Price");
                        String currency = Methods.getCurrency(itemSection);
                        EconomySession session = crazyManager.getEconomySession(currency);

                        if (session.getMoney(player) < cost) {
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

                        if (itemSection.getBoolean("Biddable")) {
                            if (Objects.equals(player.getUniqueId().toString(), itemSection.getString("TopBidder", ""))) {
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

                            openBidding(player, itemId);

                            biddingID.put(player.getUniqueId(), itemId);
                        } else {
                            playClick(player);

                            openBuying(player, itemId);
                        }

                        return;

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
                    String ID = playerBuyingId.get(player.getUniqueId());
                    playClick(player);

                    if (!data.contains("Items." + ID)) {
                        openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

                        return;
                    }

                    if (Methods.isInvFull(player)) {
                        player.closeInventory();
                        player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

                        return;
                    }

                    ConfigurationSection itemSection = data.getConfigurationSection("Items." + ID);
                    double cost = itemSection.getDouble("Price");
                    String seller = itemSection.getString("Seller");

                    String currency = Methods.getCurrency(itemSection);
                    EconomySession session = crazyManager.getEconomySession(currency);

                    Map<String, String> placeholders = new HashMap<>();

                    if (session.getMoney(player) < cost) {
                        player.closeInventory();

                        String moneyNeeded = crazyManager.getPriceWithCurrency(String.valueOf(cost - session.getMoney(player)), currency);

                        placeholders.put("%Money_Needed%", moneyNeeded);
                        placeholders.put("%money_needed%", moneyNeeded);

                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                        return;
                    }

                    if (!session.removeMoney(player, cost)) {
                        player.closeInventory();

                        String moneyNeeded = crazyManager.getPriceWithCurrency(String.valueOf(cost - session.getMoney(player)), currency);

                        placeholders.put("%Money_Needed%", moneyNeeded);
                        placeholders.put("%money_needed%", moneyNeeded);

                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                        return;
                    }

                    ItemStack i = Methods.fromBase64(itemSection.getString("Item"));

                    new AuctionBuyEvent(player, i, cost, currency).callEvent();
                    player.getInventory().addItem(i);
                    data.set(itemSection.getCurrentPath(), null);
                    Files.data.save();

                    String price = crazyManager.getPriceWithCurrency(String.valueOf(cost), currency);
                    OfflinePlayer sellerPlayer = Methods.getOfflinePlayer(seller);

                    placeholders.put("%Price%", price);
                    placeholders.put("%price%", price);
                    placeholders.put("%Player%", player.getName());
                    placeholders.put("%player%", player.getName());
                    placeholders.put("%Seller%", sellerPlayer.getName());
                    placeholders.put("%seller%", sellerPlayer.getName());
                    player.sendMessage(Messages.BOUGHT_ITEM.getMessage(player, placeholders));

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                    double taxAmount = (double) (cost * config.getDouble("Settings.Percent-Tax", 0) / 100);
                    cost -= taxAmount;

                    cost = Math.max(0, cost);

                    session.addMoney(sellerPlayer, cost);

                    String tax = String.valueOf(taxAmount);
                    String taxedPrice = crazyManager.getPriceWithCurrency(String.valueOf(cost), currency);

                    placeholders.put("%Tax%", tax);
                    placeholders.put("%tax%", tax);
                    placeholders.put("%Taxed_Price%", taxedPrice);
                    placeholders.put("%taxed_price%", taxedPrice);

                    final Player auctioneer = Methods.getPlayer(seller);

                    if (auctioneer != null) {
                        auctioneer.sendMessage(Messages.PLAYER_BOUGHT_ITEM.getMessage(player, placeholders));
                        String soundName = config.getString("Settings.Sold-Item-Sound", "UI_BUTTON_CLICK");
                        playSoldSound(auctioneer, soundName);
                    }
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

            if (playerPageItemsId.containsKey(player.getUniqueId())) {
                if (playerPageItemsId.get(player.getUniqueId()).size() >= slot) {
                    int id = playerPageItemsId.get(player.getUniqueId()).get(slot);

                    for (ConfigurationSection itemSection : crazyManager.getItems()) {
                        if (id != itemSection.getInt("StoreID")) {
                            continue;
                        }
                        player.sendMessage(Messages.CANCELLED_ITEM.getMessage(player));

                        Methods.expireItem(1, player, itemSection, data, Reasons.PLAYER_FORCE_CANCEL);

                        Files.data.save();

                        playClick(player);

                        openPlayersCurrentList(player, 1);

                        return;
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

                    for (ConfigurationSection itemSection : crazyManager.getExpiredItems(player.getUniqueId().toString())) {
                        if (Methods.isInvFull(player)) {
                            player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

                            break;
                        } else {
                            player.getInventory().addItem(Methods.fromBase64(itemSection.getString("Item")));

                            data.set(itemSection.getCurrentPath(), null);
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

            if (playerPageItemsId.containsKey(player.getUniqueId())) {
                if (playerPageItemsId.get(player.getUniqueId()).size() >= slot) {
                    int id = playerPageItemsId.get(player.getUniqueId()).get(slot);

                    for (ConfigurationSection itemSection : crazyManager.getExpiredItems()) {
                        if (id != itemSection.getInt("StoreID")) {
                            continue;
                        }
                        if (!Methods.isInvFull(player)) {
                            player.sendMessage(Messages.GOT_ITEM_BACK.getMessage(player));

                            player.getInventory().addItem(Methods.fromBase64(itemSection.getString("Item")));

                            data.set(itemSection.getCurrentPath(), null);

                            Files.data.save();

                            playClick(player);

                            openPlayersExpiredList(player, 1);
                        } else {
                            player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));
                        }

                        return;
                    }

                    playClick(player);

                    openShop(player, shopType.get(player.getUniqueId()), shopCategory.get(player.getUniqueId()), 1);

                    player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));
                }
            }
        }
        if (auctionMenu.getTitle().contains(config.getString("Settings.GUISettings.Currency.Title"))) {
            CurrencyAuctionSession currSession = currencySessions.get(player.getUniqueId());
            if (currSession == null) {
                player.closeInventory();
                return;
            }

            ItemMeta meta = item.getItemMeta();
            NamespacedKey currencyKey = new NamespacedKey(plugin, "currency");
            String currency = meta.getPersistentDataContainer().get(currencyKey, PersistentDataType.STRING);

            if (currency != null) {
                EconomySession session = crazyManager.getEconomySession(currency);
                ItemSeller.sell(session, player, currSession.getItem(), currSession.getAmount(), currSession.getPrice(), currSession.isBid(), currency);

                playClick(player);
                player.closeInventory();
            }
        }
    }
}
