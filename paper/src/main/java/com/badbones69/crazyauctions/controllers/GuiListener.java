package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.*;
import com.badbones69.crazyauctions.api.builders.gui.GuiBuilder;
import com.badbones69.crazyauctions.api.builders.gui.GuiType;
import com.badbones69.crazyauctions.api.builders.items.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.Reasons;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.api.registry.PaperButtonRegistry;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.badbones69.crazyauctions.common.enums.messages.Messages;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import us.crazycrew.api.enums.ShopType;
import com.badbones69.crazyauctions.common.enums.keys.FileKeys;
import com.badbones69.crazyauctions.api.events.AuctionBuyEvent;
import com.badbones69.crazyauctions.api.events.AuctionNewBidEvent;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import com.ryderbelserion.fusion.paper.builders.folia.Scheduler;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GuiListener implements Listener {

    private static final CrazyAuctions plugin = CrazyAuctions.get();

    private static final CrazyAuctionsPaper platform = plugin.getPlatform();

    private static final PaperButtonRegistry buttonRegistry = platform.getButtonRegistry();

    private static final FusionPaper fusion = platform.getFusion();

    private static final Map<UUID, Double> bidding = new HashMap<>();
    private static final Map<UUID, String> biddingID = new HashMap<>();
    private static final Map<UUID, ShopType> types = new HashMap<>(); // Shop Type
    private static final Map<UUID, Category> shopCategory = new HashMap<>(); // Category Type

    public static void openShop(@NotNull Player player, @NotNull ShopType shopType, @NotNull Category cat, int page) {
        final UUID uuid = player.getUniqueId();

        Methods.updateAuction();

        final YamlConfiguration config = FileKeys.config.getConfiguration();
        final YamlConfiguration data = FileKeys.data.getConfiguration();

        final List<ItemStack> items = new ArrayList<>();

        if (!data.contains("Items")) {
            data.set("Items.Clear", null);

            FileKeys.data.save();
        }

        shopCategory.put(uuid, cat);

        final ConfigurationSection section = data.getConfigurationSection("Items");

        if (section != null) {
            for (final String identifier : section.getKeys(false)) {
                final ConfigurationSection index = section.getConfigurationSection(identifier);

                if (index == null) continue;

                if (!index.contains("Item")) continue;

                if (!cat.equals(Category.NONE)) continue;

                final String store_id = index.getString("StoreID", "");

                if (store_id.isEmpty()) continue;

                final ItemBuilder itemBuilder = ItemBuilder.from(ItemType.STONE).withBase64(index.getString("Item", "")).setPersistentString(Keys.auction_store_id.getNamespacedKey(), store_id);

                final Set<String> categoryItems = cat.getItems();

                if (!categoryItems.contains(itemBuilder.asString())) continue;

                final String sellerName = index.getString("SellerName", "N/A");
                final String time = Methods.convertToTime(index.getLong("Time-Till-Expire", 0L));

                switch (shopType) {
                    case SELL -> {
                        final String format = StringUtils.formatNumber(Methods.getPrice(identifier, false));

                        for (final String line : config.getStringList("Settings.GUISettings.SellingItemLore")) {
                            itemBuilder.addDisplayLore(line);
                        }

                        items.add(itemBuilder.addPlaceholder("%Price%", format)
                                .addPlaceholder("%price%", format)
                                .addPlaceholder("%Seller%", sellerName)
                                .addPlaceholder("%seller%", sellerName)
                                .addPlaceholder("%Time%", time)
                                .addPlaceholder("%time%", time)
                                .asItemStack(player)
                        );
                    }

                    case BID -> {
                        if (index.getBoolean("Biddable", false)) {
                            final String price = StringUtils.formatNumber(Methods.getPrice(identifier, false));
                            final String topBidderName = index.getString("TopBidderName", "N/A");

                            for (final String line : config.getStringList("Settings.GUISettings.Bidding")) {
                                itemBuilder.addDisplayLore(line);
                            }

                            items.add(itemBuilder.addPlaceholder("%TopBid%", price)
                                    .addPlaceholder("%topbid%", price)
                                    .addPlaceholder("%Seller%", sellerName)
                                    .addPlaceholder("%seller%", sellerName)
                                    .addPlaceholder("%TopBidder%", topBidderName)
                                    .addPlaceholder("%topbidder%", topBidderName)
                                    .addPlaceholder("%Time%", time)
                                    .addPlaceholder("%time%", time)
                                    .asItemStack(player));
                        }
                    }
                }
            }
        }

        page = Math.min(Methods.getMaxPage(items), page);

        Inventory inv = new GuiBuilder(54, config.getString("Settings.GUIName", "&4Crazy &bAuctions&8 #{page}"), GuiType.main_menu, page).getInventory();

        final List<String> options = new ArrayList<>(java.util.List.of(
                "Cancelled/ExpiredItems",
                "PreviousPage",
                "SellingItems",
                "Category1",
                "Category2",
                "NextPage",
                "Refresh"
        ));

        types.putIfAbsent(uuid, shopType);

        switch (shopType) {
            case BID -> {
                if (platform.isBidModuleEnabled()) {
                    options.add("Bidding/Selling.Selling");
                }

                options.add("WhatIsThis.SellingShop");
            }

            case SELL -> {
                if (platform.isBidModuleEnabled()) {
                    options.add("Bidding/Selling.Bidding");
                }

                options.add("WhatIsThis.BiddingShop");
            }
        }

        final boolean hasCategory = shopCategory.containsKey(uuid);

        options.forEach(option -> buttonRegistry.getButtonByName(option).ifPresent(button -> {
            final Map<String, String> placeholders = new HashMap<>();
            final Map<NamespacedKey, String> keys = new HashMap<>();

            if (hasCategory) {
                final String name = Methods.color(shopCategory.get(uuid).getName());

                placeholders.putIfAbsent("%Category%", name);
                placeholders.putIfAbsent("%category%", name);

                keys.put(Keys.auction_category.getNamespacedKey(), name);
            }

            button.setItem(player, inv, keys, placeholders);
        }));

        setPage(inv, page, items, player);
    }

    private static void setPage(final Inventory inventory, final int page, final List<ItemStack> items, final Player player) {
        for (final ItemStack item : Methods.getPage(items, page)) {
            final int slot = inventory.firstEmpty();

            if (slot == -1) break;

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }

    public static void openCategories(@NotNull final Player player, @NotNull final ShopType shop) {
        final UUID uuid = player.getUniqueId();

        Methods.updateAuction();

        final YamlConfiguration config = FileKeys.config.getConfiguration();

        Inventory inv = new GuiBuilder(54, config.getString("Settings.Categories", "&8Categories"), GuiType.categories_menu).getInventory();

        java.util.List.of(
                "OtherSettings.WhatIsThis.Categories",
                "OtherSettings.Back",
                "Potions",
                "Weapons",
                "Blocks",
                "Other",
                "Tools",
                "Armor",
                "Food",
                "None"
        ).forEach(id -> buttonRegistry.getButtonByName(id).ifPresent(button -> button.setItem(player, inv, Map.of())));

        types.put(uuid, shop);

        player.openInventory(inv);
    }

    public static void openPlayersCurrentList(@NotNull final Player player, final int page) {
        final UUID uuid = player.getUniqueId();
        final String asString = uuid.toString();

        Methods.updateAuction();

        final YamlConfiguration config = FileKeys.config.getConfiguration();
        final List<String> configLore = config.getStringList("Settings.GUISettings.CurrentLore");
        final YamlConfiguration data = FileKeys.data.getConfiguration();

        final List<ItemStack> items = new ArrayList<>();

        final Inventory inv = new GuiBuilder(54, config.getString("Settings.Players-Current-Items", "&8Your Current Listings"), GuiType.current_menu).getInventory();

        java.util.List.of(
                "WhatIsThis.CurrentItems",
                "Back"
        ).forEach(id -> buttonRegistry.getButtonByName(id).ifPresent(button -> button.setItem(player, inv, Map.of())));

        final ConfigurationSection section = data.getConfigurationSection("Items");

        if (section != null) {
            for (final String identifier : section.getKeys(false)) {
                final ConfigurationSection index = section.getConfigurationSection(identifier);

                if (index == null) {
                    continue;
                }

                final String user = index.getString("Seller", "");

                if (user.isEmpty()) {
                    fusion.log(Level.WARNING, "Seller cannot be blank @ %s", identifier);

                    continue;
                }

                final String item = index.getString("Item", "");

                if (item.isEmpty()) {
                    fusion.log(Level.WARNING, "Item cannot be blank @ %s", identifier);

                    continue;
                }

                final String store_id = index.getString("StoreID", "");

                if (store_id.isEmpty()) {
                    fusion.log(Level.WARNING, "Store ID cannot be blank @ %s", identifier);

                    continue;
                }

                if (!asString.equals(user)) {
                    continue;
                }

                final String price = StringUtils.formatNumber(Methods.getPrice(identifier, false));

                final String time = Methods.convertToTime(index.getLong("Time-Till-Expire"));

                final ItemBuilder builder = ItemBuilder.from(ItemType.STONE).withBase64(index.getString("Item", "")).setPersistentString(Keys.auction_store_id.getNamespacedKey(), store_id);

                for (final String line : configLore) {
                    builder.addDisplayLore(fusion.replacePlaceholders(line, Map.of(
                            "%Price%", price,
                            "%price%", price,
                            "%Time%", time,
                            "%time%", time
                    )));
                }

                items.add(builder.asItemStack(player));
            }
        }

        setPage(inv, page, items, player);
    }

    public static void openPlayersExpiredList(@NotNull final Player player, int page) {
        final UUID uuid = player.getUniqueId();
        final String asString = uuid.toString();

        Methods.updateAuction();

        final YamlConfiguration config = FileKeys.config.getConfiguration();

        final List<String> configLore = config.getStringList("Settings.GUISettings.Cancelled/ExpiredLore");

        final YamlConfiguration data = FileKeys.data.getConfiguration();

        final List<ItemStack> items = new ArrayList<>();

        final ConfigurationSection section = data.getConfigurationSection("OutOfTime/Cancelled");

        if (section != null) {
            for (final String identifier : section.getKeys(false)) {
                final ConfigurationSection index = section.getConfigurationSection(identifier);

                if (index == null) {
                    continue;
                }

                final String user = index.getString("Seller", "");

                if (user.isEmpty()) {
                    fusion.log(Level.WARNING, "Seller cannot be blank @ %s", identifier);

                    continue;
                }

                final String item = index.getString("Item", "");

                if (item.isEmpty()) {
                    fusion.log(Level.WARNING, "Item cannot be blank @ %s", identifier);

                    continue;
                }

                final String store_id = index.getString("StoreID", "");

                if (store_id.isEmpty()) {
                    fusion.log(Level.WARNING, "Store ID cannot be blank @ %s", identifier);

                    continue;
                }

                if (!asString.equals(user)) {
                    continue;
                }

                final String price = StringUtils.formatNumber(Methods.getPrice(identifier, false));
                final String time = Methods.convertToTime(index.getLong("Full-Time"));

                final ItemBuilder builder = ItemBuilder.from(ItemType.STONE).withBase64(item);

                for (final String line : configLore) {
                    builder.addDisplayLore(fusion.replacePlaceholders(line, Map.of(
                            "%Price%", price,
                            "%price%", price,
                            "%Time%", time,
                            "%time%", time
                    )));
                }

                items.add(builder.asItemStack(player));
            }
        }

        page = Math.min(Methods.getMaxPage(items), page);

        final Inventory inv = new GuiBuilder(54, config.getString("Settings.Cancelled/Expired-Items", "&8Canceled/Expired Listings #{page}"), GuiType.expired_menu, page).getInventory();

        java.util.List.of(
                "WhatIsThis.Cancelled/ExpiredItems",
                "PreviousPage",
                "NextPage",
                "Return",
                "Back"
        ).forEach(id -> buttonRegistry.getButtonByName(id).ifPresent(button -> {
            final Map<NamespacedKey, String> values = new HashMap<>();

            /*switch (id) { //todo() store page on the pdc.
                case "PreviousPage", "Back", "NextPage", "Return" -> values.put(Keys.auction_button.getNamespacedKey(), id);
            }*/

            button.setItem(player, inv, values);
        }));

        setPage(inv, page, items, player);
    }

    public static void openBuying(@NotNull Player player, @NotNull String id) {
        Methods.updateAuction();

        final YamlConfiguration config = FileKeys.config.getConfiguration();
        final YamlConfiguration data = FileKeys.data.getConfiguration();

        final UUID uuid = player.getUniqueId();

        if (!data.contains("Items." + id)) {
            openShop(player, ShopType.SELL, shopCategory.get(uuid), 1);

            Messages.item_doesnt_exist.sendMessage(player);

            return;
        }

        Inventory inv = new GuiBuilder(9, config.getString("Settings.Buying-Item", "&8Purchase Item: Are You Sure?"), GuiType.buy_menu).getInventory();

        java.util.List.of(
                "Confirm",
                "Cancel"
        ).forEach(option -> buttonRegistry.getButtonByName(option).ifPresent(button -> {
            final Map<NamespacedKey, String> keys = new HashMap<>();
            final List<Integer> slots = new ArrayList<>();

            switch (option) {
                case "Confirm" -> {
                    slots.add(0);
                    slots.add(1);
                    slots.add(2);
                    slots.add(3);

                    keys.putIfAbsent(Keys.auction_button.getNamespacedKey(), "Confirm");
                }

                case "Cancel" -> {
                    slots.add(5);
                    slots.add(6);
                    slots.add(7);
                    slots.add(8);

                    keys.putIfAbsent(Keys.auction_button.getNamespacedKey(), "Cancel");
                }
            }

            button.setItem(player, inv, keys, slots);
        }));

        final String price = StringUtils.formatNumber(Methods.getPrice(id, false));
        final String time = Methods.convertToTime(data.getLong("Items." + id + ".Time-Till-Expire"));

        final String sellerName = data.getString("Items." + id + ".SellerName", "N/A");

        final ItemBuilder itemBuilder = ItemBuilder.from(ItemType.STONE).withBase64(data.getString("Items." + id + ".Item", "")).setPersistentString(Keys.auction_store_id.getNamespacedKey(), id);

        for (final String line : config.getStringList("Settings.GUISettings.SellingItemLore")) {
            itemBuilder.addDisplayLore(line);
        }

        inv.setItem(4, itemBuilder
                .addPlaceholder("%Price%", price)
                .addPlaceholder("%price%", price)
                .addPlaceholder("%Seller%", sellerName)
                .addPlaceholder("%seller%", sellerName)
                .addPlaceholder("%Time%", time)
                .addPlaceholder("%time%", time)
                .asItemStack(player));

        player.openInventory(inv);
    }

    public static void openBidding(@NotNull final Player player, @NotNull final String id) {
        Methods.updateAuction();

        YamlConfiguration config = FileKeys.config.getConfiguration();
        YamlConfiguration data = FileKeys.data.getConfiguration();

        final UUID uuid = player.getUniqueId();

        if (!data.contains("Items." + id)) {
            openShop(player, ShopType.BID, shopCategory.get(uuid), 1);

            Messages.item_doesnt_exist.sendMessage(player);

            return;
        }

        Inventory inv = new GuiBuilder(27, config.getString("Settings.Bidding-On-Item", "&8You Are Bidding On This Item."), GuiType.bid_menu).getInventory();

        bidding.putIfAbsent(uuid, (double) Methods.getPrice(id, false));

        inv.setItem(9, ItemBuilder.from(ItemType.LIME_STAINED_GLASS_PANE).withDisplayName("&a+1").setAmount(1).asItemStack());
        inv.setItem(10, ItemBuilder.from(ItemType.LIME_STAINED_GLASS_PANE).withDisplayName("&a+10").setAmount(1).asItemStack());
        inv.setItem(11, ItemBuilder.from(ItemType.LIME_STAINED_GLASS_PANE).withDisplayName("&a+100").setAmount(1).asItemStack());
        inv.setItem(12, ItemBuilder.from(ItemType.LIME_STAINED_GLASS_PANE).withDisplayName("&a+1000").setAmount(1).asItemStack());
        inv.setItem(14, ItemBuilder.from(ItemType.LIME_STAINED_GLASS_PANE).withDisplayName("&c-1000").setAmount(1).asItemStack());
        inv.setItem(15, ItemBuilder.from(ItemType.LIME_STAINED_GLASS_PANE).withDisplayName("&c-100").setAmount(1).asItemStack());
        inv.setItem(16, ItemBuilder.from(ItemType.LIME_STAINED_GLASS_PANE).withDisplayName("&c-10").setAmount(1).asItemStack());
        inv.setItem(17, ItemBuilder.from(ItemType.LIME_STAINED_GLASS_PANE).withDisplayName("&c-1").setAmount(1).asItemStack());

        buttonRegistry.getButtonByName("Bidding").ifPresent(button -> {
            final String price = StringUtils.formatNumber(Methods.getPrice(id, false));
            final String bid = String.valueOf(bidding.get(uuid));

            button.setItem(player, inv, 13, Map.of(), Map.of(
                    "%Bid%", bid,
                    "%bid%", bid,
                    "%TopBid%", price,
                    "%topbid%", price
            ));
        });

        buttonRegistry.getButtonByName("Bid").ifPresent(button -> button.setItem(player, inv, 22, Map.of()));

        inv.setItem(4, getBiddingItem(id));

        player.openInventory(inv);
    }

    public static void openViewer(@NotNull Player player, int page) {
        Methods.updateAuction();

        final YamlConfiguration config = FileKeys.config.getConfiguration();

        final List<String> sellingLore = config.getStringList("Settings.GUISettings.SellingItemLore");
        final List<String> biddingLore = config.getStringList("Settings.GUISettings.Bidding");

        final YamlConfiguration data = FileKeys.data.getConfiguration();

        final List<ItemStack> items = new ArrayList<>();

        final UUID uuid = player.getUniqueId();
        final String asString = uuid.toString();

        if (!data.contains("Items")) {
            data.set("Items.Clear", null);

            FileKeys.data.save();
        }

        final ConfigurationSection section = data.getConfigurationSection("Items");

        if (section != null) {
            for (final String identifier : section.getKeys(false)) {
                final ConfigurationSection index = section.getConfigurationSection(identifier);

                if (index == null) {
                    continue;
                }

                final String user = index.getString("Seller", "");

                if (user.isEmpty()) {
                    fusion.log(Level.WARNING, "Seller cannot be empty for %s", identifier);

                    continue;
                }

                if (!asString.equals(user)) {
                    continue;
                }

                final String store_id = index.getString("StoreID", "");

                if (store_id.isEmpty()) {
                    fusion.log(Level.WARNING, "Store ID cannot be empty for %s", identifier);

                    continue;
                }

                final String price = StringUtils.formatNumber(Methods.getPrice(identifier, false));
                final String time = Methods.convertToTime(index.getLong("Time-Till-Expire", 0L));

                final String sellerName = index.getString("SellerName", "N/A");
                final String bidderName = index.getString("TopBidderName", "N/A");

                final ItemBuilder builder = ItemBuilder.from(ItemType.STONE).withBase64(index.getString("Item", "")).setPersistentString(Keys.auction_button.getNamespacedKey(), store_id);

                final boolean isBiddable = index.getBoolean("Biddable", false);

                final Map<String, String> placeholders = new HashMap<>();

                if (isBiddable) {
                    placeholders.putIfAbsent("%TopBid%", price);
                    placeholders.putIfAbsent("%TopBidder", bidderName);
                }

                placeholders.putIfAbsent("%Price%", price);
                placeholders.putIfAbsent("%price%", price);
                placeholders.putIfAbsent("%Seller%", sellerName);
                placeholders.putIfAbsent("%seller%", sellerName);
                placeholders.putIfAbsent("%Time%", time);
                placeholders.putIfAbsent("%time%", time);

                for (final String line : isBiddable ? biddingLore : sellingLore) {
                    builder.addDisplayLore(fusion.replacePlaceholders(line, placeholders));
                }

                items.add(builder.asItemStack(player));
            }
        }

        int maxPage = Methods.getMaxPage(items);

        page = Math.min(maxPage, page);

        final Inventory inv = new GuiBuilder(54, config.getString("Settings.GUIName", "&4Crazy &bAuctions&8 #{page}"), GuiType.main_menu, page).getInventory();

        buttonRegistry.getButtonByName("WhatIsThis.Viewing").ifPresent(button -> button.setItem(player, inv, Map.of()));

        setPage(inv, page, items, player);
    }

    private static ItemStack getBiddingItem(@NotNull String ID) {
        final YamlConfiguration config = FileKeys.config.getConfiguration();
        final YamlConfiguration data = FileKeys.data.getConfiguration();

        final String price = StringUtils.formatNumber(Methods.getPrice(ID, false));
        final String time = Methods.convertToTime(data.getLong("Items." + ID + ".Time-Till-Expire"));

        final String sellerName = data.getString("Items." + ID + ".SellerName", "N/A");
        final String bidderName = data.getString("Items." + ID + ".TopBidderName", "N/A");

        final ItemBuilder itemBuilder = ItemBuilder.from(ItemType.STONE).withBase64(data.getString("Items." + ID + ".Item", "")).setPersistentString(Keys.auction_store_id.getNamespacedKey(), ID);

        for (String line : config.getStringList("Settings.GUISettings.Bidding")) {
            itemBuilder.addDisplayLore(fusion.replacePlaceholders(line, Map.of(
                    "%TopBid%", price,
                    "%topbid%", price,
                    "%Seller%", sellerName,
                    "%seller%", sellerName,
                    "%TopBidder%", bidderName,
                    "%topbidder%", bidderName,
                    "%Time%", time,
                    "%time%", time
            )));
        }

        return itemBuilder.asItemStack();
    }

    private static void playClick(@NotNull Player player) {
        final YamlConfiguration config = FileKeys.config.getConfiguration();

        if (config.getBoolean("Settings.Sounds.Toggle", false)) {
            final String sound = config.getString("Settings.Sounds.Sound", "");

            final Sound soundToPlay = Registry.SOUNDS.get(NamespacedKey.minecraft(sound));

            if (soundToPlay == null) return;

            player.playSound(player.getLocation(), soundToPlay, 1, 1);
        }
    }

    private void playSoldSound(@NotNull Player player) {
        final YamlConfiguration config = FileKeys.config.getConfiguration();

        final String sound = config.getString("Settings.Sold-Item-Sound", "");

        final Sound soundToPlay = Registry.SOUNDS.get(NamespacedKey.minecraft(sound));

        if (soundToPlay == null) return;

        player.playSound(player.getLocation(), soundToPlay, 1, 1);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof GuiBuilder auctionMenu)) return;

        if (!(event.getPlayer() instanceof Player player)) return;

        final YamlConfiguration config = FileKeys.config.getConfiguration();
        final String title = auctionMenu.getTitle();

        if (title.contains(Methods.color(config.getString("Settings.Bidding-On-Item", "")))) bidding.remove(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof GuiBuilder auctionMenu)) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        event.setCancelled(true);

        final YamlConfiguration config = FileKeys.config.getConfiguration();
        final YamlConfiguration data = FileKeys.data.getConfiguration();

        final UUID uuid = player.getUniqueId();

        final int slot = event.getRawSlot();

        if (slot > inventory.getSize()) return;

        final ItemStack item = event.getCurrentItem();

        if (item == null) return;

        final PersistentDataContainerView container = item.getPersistentDataContainer();

        if (!container.has(Keys.auction_button.getNamespacedKey())) {
            return;
        }

        final String variable = container.getOrDefault(Keys.auction_button.getNamespacedKey(), PersistentDataType.STRING, "");

        if (variable.isEmpty()) {
            return;
        }

        final String id = container.getOrDefault(Keys.auction_store_id.getNamespacedKey(), PersistentDataType.STRING, "");
        final GuiType guiType = auctionMenu.getType();

        switch (guiType) {
            case categories_menu -> {
                final Category category = Category.getFromName(variable);

                if (category != null) {
                    openShop(player, types.get(uuid), category, 1);

                    playClick(player);

                    return;
                }

                if (variable.equalsIgnoreCase("Back")) {
                    openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                    playClick(player);
                }
            }

            case current_menu -> {
                if (variable.equalsIgnoreCase("Back")) {
                    openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                    playClick(player);

                    return;
                }

                final ConfigurationSection itemsSection = data.getConfigurationSection("Items");

                if (itemsSection == null) {
                    playClick(player);

                    openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                    Messages.item_doesnt_exist.sendMessage(player);

                    return;
                }

                final ConfigurationSection index = itemsSection.getConfigurationSection(id);

                if (index == null) {
                    playClick(player);

                    openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                    Messages.item_doesnt_exist.sendMessage(player);

                    return;
                }

                Messages.cancelled_item.sendMessage(player);

                Methods.expireItem(1, player, id, data, Reasons.PLAYER_FORCE_CANCEL);

                FileKeys.data.save();

                playClick(player);

                openPlayersCurrentList(player, 1);
            }

            case expired_menu -> {
                switch (variable) {
                    case "Back" -> {
                        Methods.updateAuction();

                        openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                        playClick(player);
                    }

                    case "PreviousPage" -> {
                        Methods.updateAuction();

                        int page = auctionMenu.getPageNumber();

                        if (page == 1) page++;

                        playClick(player);

                        openPlayersExpiredList(player, (page - 1));
                    }

                    case "Return" -> {
                        Methods.updateAuction();

                        int page = auctionMenu.getPageNumber();

                        final ConfigurationSection section = data.getConfigurationSection("OutOfTime/Cancelled");

                        boolean isSave = false;

                        if (section != null) {
                            for (final String identifier : section.getKeys(false)) {
                                final ConfigurationSection index = section.getConfigurationSection(identifier);

                                if (index == null) {
                                    continue;
                                }

                                final String seller = index.getString("Seller", "");

                                if (!seller.equals(uuid.toString())) {
                                    continue;
                                }

                                if (Methods.isInvFull(player)) {
                                    Messages.inventory_full.sendMessage(player);

                                    break;
                                }

                                final PlayerInventory playerInventory = player.getInventory();

                                playerInventory.addItem(Methods.fromBase64(index.getString("Item", "")));

                                section.set(identifier, null);

                                isSave = true;
                            }
                        }

                        if (isSave) {
                            Messages.got_item_back.sendMessage(player);

                            FileKeys.data.save();

                            playClick(player);

                            openPlayersExpiredList(player, page);
                        }
                    }

                    case "NextPage" -> {
                        Methods.updateAuction();

                        int page = auctionMenu.getPageNumber();

                        playClick(player);

                        openPlayersExpiredList(player, (page + 1));
                    }

                    default -> {
                        final ConfigurationSection section = data.getConfigurationSection("OutOfTime/Cancelled");

                        if (section == null) {
                            playClick(player);

                            openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                            Messages.item_doesnt_exist.sendMessage(player);

                            return;
                        }

                        if (id.isEmpty()) {
                            return;
                        }

                        final ConfigurationSection index = section.getConfigurationSection(id);

                        if (index == null) {
                            return;
                        }

                        if (Methods.isInvFull(player)) {
                            Messages.inventory_full.sendMessage(player);

                            return;
                        }

                        final PlayerInventory playerInventory = player.getInventory();

                        playerInventory.addItem(Methods.fromBase64(index.getString("Item", "")));

                        section.set(id, null);

                        FileKeys.data.save();

                        Messages.got_item_back.sendMessage(player);

                        playClick(player);

                        openPlayersExpiredList(player, 1);
                    }
                }
            }

            case main_menu -> {
                int pageNumber = auctionMenu.getPageNumber();

                switch (variable) {
                    case "PreviousPage" -> {
                        Methods.updateAuction();

                        if (pageNumber == 1) pageNumber++;

                        openShop(player, types.get(uuid), shopCategory.get(uuid), pageNumber - 1);

                        playClick(player);
                    }

                    case "NextPage" -> {
                        Methods.updateAuction();

                        openShop(player, types.get(uuid), shopCategory.get(uuid), pageNumber + 1);

                        playClick(player);
                    }

                    case "Refresh", "Refesh" -> {
                        Methods.updateAuction();

                        openShop(player, types.get(uuid), shopCategory.get(uuid), pageNumber);

                        playClick(player);
                    }

                    case "Bidding/Selling.Bidding" -> {
                        openShop(player, ShopType.SELL, shopCategory.get(uuid), 1);

                        playClick(player);
                    }

                    case "Bidding/Selling.Selling" -> {
                        openShop(player, ShopType.BID, shopCategory.get(uuid), 1);

                        playClick(player);
                    }

                    case "Cancelled/ExpiredItems" -> {
                        openPlayersExpiredList(player, 1);

                        playClick(player);
                    }

                    case "SellingItems" -> {
                        openPlayersCurrentList(player, 1);

                        playClick(player);
                    }

                    case "Category1", "Category2" -> {
                        openCategories(player, types.get(uuid));

                        playClick(player);
                    }

                    case "Your-Item", "Top-Bidder", "Cant-Afford" -> {}

                    default -> {
                        final ConfigurationSection itemsSection = data.getConfigurationSection("Items");

                        if (itemsSection == null) {
                            return;
                        }

                        final ConfigurationSection index = itemsSection.getConfigurationSection(id);

                        if (index == null) {
                            playClick(player);

                            openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                            Messages.item_doesnt_exist.sendMessage(player);

                            return;
                        }

                        final String human = index.getString("Seller", "");

                        if (human.isEmpty()) {
                            fusion.log(Level.WARNING, "Seller cannot be empty for %s", id);

                            return;
                        }

                        final boolean hasPermission = Permissions.admin_wildcard.hasPermission(player) || Permissions.force_end.hasPermission(player);
                        final InventoryAction action = event.getAction();

                        if (hasPermission && action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                            final OfflinePlayer seller = Methods.getOfflinePlayer(human);

                            if (seller.getPlayer() != null) { //todo() optionals
                                Messages.admin_force_cancelled_to_player.sendMessage(seller.getPlayer());
                            }

                            Methods.expireItem(1, seller, id, data, Reasons.ADMIN_FORCE_CANCEL);

                            FileKeys.data.save();

                            Messages.admin_force_cancelled.sendMessage(player);

                            playClick(player);

                            openShop(player, types.get(uuid), shopCategory.get(uuid), auctionMenu.getPageNumber());

                            return;
                        }

                        if (uuid.toString().equals(human)) {
                            buttonRegistry.getButtonByName("Your-Item").ifPresent(button -> button.setItem(player, inventory, slot, Map.of()));

                            playClick(player);

                            new FoliaScheduler(plugin, Scheduler.global_scheduler) {
                                @Override
                                public void run() {
                                    inventory.setItem(slot, item);
                                }
                            }.runDelayed(3 * 20);

                            return;
                        }

                        final VaultSupport support = plugin.getSupport();
                        final double money = support.getMoney(player);
                        final long price = index.getLong("Price", 0L);

                        if (price <= 0) {
                            fusion.log(Level.WARNING, "Price cannot be less than or equal to 0 for %s", id);

                            return;
                        }

                        if (money < price) {
                            buttonRegistry.getButtonByName("Cant-Afford").ifPresent(button -> button.setItem(player, inventory, slot, Map.of()));

                            playClick(player);

                            new FoliaScheduler(plugin, Scheduler.global_scheduler) {
                                @Override
                                public void run() {
                                    inventory.setItem(slot, item);
                                }
                            }.runDelayed(3 * 20);

                            return;
                        }

                        if (index.getBoolean("Biddable", false)) {
                            final String topBidder = index.getString("TopBidder", "");

                            if (uuid.toString().equals(topBidder)) {
                                buttonRegistry.getButtonByName("Top-Bidder").ifPresent(button -> button.setItem(player, inventory, slot, Map.of()));

                                playClick(player);

                                new FoliaScheduler(plugin, Scheduler.global_scheduler) {
                                    @Override
                                    public void run() {
                                        inventory.setItem(slot, item);
                                    }
                                }.runDelayed(3 * 20);

                                return;
                            }

                            playClick(player);

                            openBidding(player, id);

                            biddingID.put(uuid, id);
                        } else {
                            playClick(player);

                            openBuying(player, id);
                        }
                    }
                }
            }

            case buy_menu -> {
                switch (variable) {
                    case "Confirm" -> {
                        long cost = data.getLong("Items." + id + ".Price", 0L);

                        if (cost <= 0) {
                            fusion.log(Level.WARNING, "Price cannot be less than or equal to 0 for %s", id);

                            playClick(player);

                            openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                            return;
                        }

                        final String seller = data.getString("Items." + id + ".Seller", "");

                        if (seller.isEmpty()) {
                            fusion.log(Level.WARNING, "Seller cannot be empty for %s", id);

                            playClick(player);

                            openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                            return;
                        }

                        if (!data.contains("Items." + id)) {
                            playClick(player);

                            openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                            Messages.item_doesnt_exist.sendMessage(player);

                            return;
                        }

                        if (Methods.isInvFull(player)) {
                            playClick(player);

                            player.closeInventory();

                            Messages.inventory_full.sendMessage(player);

                            return;
                        }

                        final VaultSupport vaultSupport = plugin.getSupport();
                        final double money = vaultSupport.getMoney(player);

                        Map<String, String> placeholders = new HashMap<>();

                        if (money < cost) {
                            playClick(player);

                            player.closeInventory();

                            placeholders.put("%Money_Needed%", (cost - money) + "");
                            placeholders.put("%money_needed%", (cost - money) + "");

                            Messages.need_more_money.sendMessage(player, placeholders);

                            return;
                        }

                        final ItemStack itemStack = Methods.fromBase64(data.getString("Items." + id + ".Item", ""));

                        new AuctionBuyEvent(player, item, cost).callEvent();

                        if (!vaultSupport.removeMoney(player, cost)) {
                            playClick(player);

                            player.closeInventory();

                            placeholders.put("%Money_Needed%", (cost - money) + "");
                            placeholders.put("%money_needed%", (cost - money) + "");

                            Messages.need_more_money.sendMessage(player, placeholders);

                            return;
                        }

                        String price = String.valueOf(cost);

                        long taxAmount = cost * config.getLong("Settings.Percent-Tax", 0) / 100;
                        cost -= taxAmount;

                        cost = Math.max(0, cost);

                        OfflinePlayer sellerPlayer = Methods.getOfflinePlayer(seller);
                        vaultSupport.addMoney(sellerPlayer, cost);

                        String tax = String.valueOf(taxAmount);
                        String taxedPrice = String.valueOf(cost);

                        placeholders.put("%Price%", price);
                        placeholders.put("%price%", price);
                        placeholders.put("%Tax%", tax);
                        placeholders.put("%tax%", tax);
                        placeholders.put("%Taxed_Price%", taxedPrice);
                        placeholders.put("%taxed_price%", taxedPrice);
                        placeholders.put("%Player%", player.getName());
                        placeholders.put("%player%", player.getName());
                        placeholders.put("%Seller%", sellerPlayer.getName());
                        placeholders.put("%seller%", sellerPlayer.getName());

                        Messages.bought_item.sendMessage(player, placeholders);

                        final Player auctioneer = Methods.getPlayer(seller);

                        if (auctioneer != null) {
                            Messages.player_bought_item.sendMessage(auctioneer, placeholders);

                            playSoldSound(auctioneer);
                        }

                        player.getInventory().addItem(itemStack);

                        data.set("Items." + id, null);

                        FileKeys.data.save();

                        playClick(player);

                        openShop(player, types.get(uuid), shopCategory.get(uuid), 1);
                    }

                    case "Cancel" -> {
                        openShop(player, types.get(uuid), shopCategory.get(uuid), 1);

                        playClick(player);
                    }
                }
            }

            case bid_menu -> {
                final ConfigurationSection itemsSection = data.getConfigurationSection("Items");

                if (variable.equalsIgnoreCase("Bid") && itemsSection != null) {
                    final ConfigurationSection items = itemsSection.getConfigurationSection(id);

                    if (items == null) {
                        return;
                    }

                    double bid = bidding.get(uuid);

                    final String topBidder = items.getString("TopBidder", "None");

                    final VaultSupport vaultSupport = plugin.getSupport();
                    final double money = vaultSupport.getMoney(player);

                    if (money < bid) {
                        final Map<String, String> placeholders = new HashMap<>();

                        placeholders.put("%Money_Needed%", (bid - money) + "");
                        placeholders.put("%money_needed%", (bid - money) + "");

                        Messages.need_more_money.sendMessage(player, placeholders);

                        return;
                    }

                    final long price = items.getLong("Price", 0);

                    if (price <= 0) {
                        fusion.log(Level.WARNING, "Price cannot be less than or equal to 0 for %s", id);

                        return;
                    }

                    if (price > bid) {
                        Messages.bid_more_money.sendMessage(player);

                        return;
                    }

                    if (price >= bid && !topBidder.equalsIgnoreCase("None")) {
                        Messages.bid_more_money.sendMessage(player);

                        return;
                    }

                    new AuctionNewBidEvent(player, Methods.fromBase64(items.getString("Item", "")), bid).callEvent();

                    items.set("Price", bid);
                    items.set("TopBidder", uuid.toString());
                    items.set("TopBidderName", player.getName());

                    final Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("%Bid%", String.valueOf(bid));

                    Messages.bid_msg.sendMessage(player, placeholders);

                    FileKeys.data.save();

                    bidding.put(uuid, 0.0);

                    player.closeInventory();

                    playClick(player);

                    return;
                }

                final Map<String, Integer> priceEdits = new HashMap<>();

                priceEdits.put("&a+1", 1);
                priceEdits.put("&a+10", 10);
                priceEdits.put("&a+100", 100);
                priceEdits.put("&a+1000", 1000);
                priceEdits.put("&c-1", -1);
                priceEdits.put("&c-10", -10);
                priceEdits.put("&c-100", -100);
                priceEdits.put("&c-1000", -1000);

                for (String price : priceEdits.keySet()) {
                    try {
                        bidding.put(uuid, (bidding.get(uuid) + priceEdits.get(price)));

                        inventory.setItem(4, getBiddingItem(biddingID.get(uuid)));

                        buttonRegistry.getButtonByName("Bidding").ifPresent(button -> {
                            final String value = StringUtils.formatNumber(Methods.getPrice(biddingID.get(uuid), false));
                            final String bid = String.valueOf(bidding.get(uuid));

                            button.setItem(player, inventory, 13, Map.of(
                                    Keys.auction_button.getNamespacedKey(), bid
                            ), Map.of(
                                    "%Bid%", bid,
                                    "%bid%", bid,
                                    "%TopBid%", value,
                                    "%topbid%", value
                            ));
                        });

                        playClick(player);

                        return;
                    } catch (final Exception exception) {
                        player.closeInventory();

                        Messages.item_doesnt_exist.sendMessage(player);

                        return;
                    }
                }
            }
        }
    }
}