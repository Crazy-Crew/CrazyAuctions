package com.badbones69.crazyauctions.api.guis.types;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.api.guis.Holder;
import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.api.GuiManager;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.badbones69.crazyauctions.tasks.InventoryManager;
import com.badbones69.crazyauctions.tasks.objects.AuctionItem;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionsMenu extends Holder {

    private List<AuctionItem> items;
    private List<String> options;
    private int maxPages;

    private FileConfiguration config;
    private FileConfiguration data;

    private Category category;

    public AuctionsMenu(final Player player, final ShopType shopType, final Category category, final String title, final int size, final int page) {
        super(player, shopType, title, size, page);

        this.items = new ArrayList<>();
        this.options = new ArrayList<>();

        this.config = Files.config.getConfiguration();
        this.data = Files.data.getConfiguration();

        if (!this.data.contains("Items")) {
            this.data.set("Items.Clear", null);

            Files.data.save();
        }

        if (category != null) {
            HolderManager.addShopCategory(player, this.category = category);
        }
    }

    private String target;

    public AuctionsMenu(final Player player, final String target, final String title, final int size, final int page) {
        this(player, null, null, title, size, page);

        this.target = target;
    }

    public AuctionsMenu() {}

    @Override
    public final Holder build() {
        if (this.target != null) {
            this.options.add("WhatIsThis.Viewing");
        } else {
            this.options.addAll(List.of(
                    "SellingItems",
                    "Cancelled/ExpiredItems",
                    "PreviousPage",
                    "Refresh",
                    "Refesh",
                    "NextPage",
                    "Category1",
                    "Category2"
            ));
        }

        calculateItems();

        HolderManager.addShopType(this.player, this.shopType);

        switch (this.shopType) {
            case SELL -> {
                if (this.crazyManager.isSellingEnabled()) {
                    this.options.add("Bidding/Selling.Selling");
                }

                this.options.add("WhatIsThis.SellingShop");
            }

            case BID -> {
                if (this.crazyManager.isBiddingEnabled()) {
                    this.options.add("Bidding/Selling.Bidding");
                }

                this.options.add("WhatIsThis.BiddingShop");
            }
        }

        for (final String key : this.options) {
            if (!this.config.contains("Settings.GUISettings.OtherSettings." + key)) {
                continue;
            }

            if (!this.config.getBoolean("Settings.GUISettings.OtherSettings." + key + ".Toggle", true)) {
                continue;
            }

            final String id = this.config.getString("Settings.GUISettings.OtherSettings." + key + ".Item");
            final String name = this.config.getString("Settings.GUISettings.OtherSettings." + key + ".Name");
            final List<String> lore = new ArrayList<>();
            final int slot = this.config.getInt("Settings.GUISettings.OtherSettings." + key + ".Slot");
            final String cName = Methods.color(this.config.getString("Settings.GUISettings.Category-Settings." + HolderManager.getShopCategory(this.player).getName() + ".Name"));

            final ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (this.config.contains("Settings.GUISettings.OtherSettings." + key + ".Lore")) {
                for (final String line : this.config.getStringList("Settings.GUISettings.OtherSettings." + key + ".Lore")) {
                    lore.add(line.replace("%Category%", cName).replace("%category%", cName));
                }
            }

            switch (key) {
                case "NextPage" -> this.inventory.setItem(slot - 1, InventoryManager.getNextButton(this.player, this).setLore(lore).build());

                case "PreviousPage" -> this.inventory.setItem(slot - 1, InventoryManager.getBackButton(this.player, this).setLore(lore).build());

                default -> this.inventory.setItem(slot - 1, itemBuilder.setLore(lore).addString(key, Keys.auction_button.getNamespacedKey()).build());
            }
        }

        for (final AuctionItem item : getPageItems(this.items, getPage(), getSize())) {
            int slot = this.inventory.firstEmpty();

            this.inventory.setItem(slot, item.getActiveItem(this.shopType).build());
        }

        this.player.openInventory(this.inventory);

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof AuctionsMenu menu)) return;

        event.setCancelled(true);

        final int slot = event.getSlot();

        final Inventory inventory = menu.getInventory();

        if (slot > inventory.getSize()) return;

        if (event.getCurrentItem() == null) return;

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) return;

        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        final Player player = (Player) event.getWhoClicked();

        FileConfiguration config = Files.config.getConfiguration();

        if (container.has(Keys.auction_button.getNamespacedKey())) {
            String type = container.getOrDefault(Keys.auction_button.getNamespacedKey(), PersistentDataType.STRING, menu.target == null ? "Refresh" : "");

            if (menu.target == null && !type.isEmpty()) {
                switch (type) {
                    case "Your-Item", "Top-Bidder", "Cant-Afford" -> {
                        menu.click(player);

                        return;
                    }

                    case "NextPage" -> {
                        menu.click(player);

                        if (menu.getPage() >= menu.maxPages) {
                            return;
                        }

                        menu.nextPage();

                        GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), menu.getPage());

                        return;
                    }

                    case "PreviousPage" -> {
                        menu.click(player);

                        final int page = menu.getPage();

                        if (page <= 1) {
                            return;
                        }

                        menu.backPage();

                        GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), menu.getPage());

                        return;
                    }

                    case "Refesh", "Refresh" -> {
                        menu.click(player);

                        GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), menu.getPage());

                        return;
                    }

                    case "Bidding/Selling.Selling" -> {
                        menu.click(player);

                        GuiManager.openShop(player, ShopType.BID, HolderManager.getShopCategory(player), 1);

                        return;
                    }

                    case "Bidding/Selling.Bidding" -> {
                        menu.click(player);

                        GuiManager.openShop(player, ShopType.SELL, HolderManager.getShopCategory(player), 1);

                        return;
                    }

                    case "Cancelled/ExpiredItems" -> {
                        menu.click(player);

                        GuiManager.openPlayersExpiredList(player, 1);

                        return;
                    }

                    case "SellingItems" -> {
                        menu.click(player);

                        GuiManager.openPlayersCurrentList(player, 1);

                        return;
                    }

                    case "Category1", "Category2" -> {
                        menu.click(player);

                        GuiManager.openCategories(player, HolderManager.getShopType(player));

                        return;
                    }
                }
            }
        }

        final UUID uuid = player.getUniqueId();

        final AuctionItem auction = this.userManager.getAuctionItemById(uuid, container.getOrDefault(Keys.auction_store_id.getNamespacedKey(), PersistentDataType.STRING, ""));

        if (auction == null) return;

        if (uuid.toString().equalsIgnoreCase(auction.getUuid().toString())) {
            String itemName = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Item");
            String name = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Name");

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings.OtherSettings.Your-Item.Lore")) {
                itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings.Your-Item.Lore"));
            }

            inventory.setItem(slot, itemBuilder.build());

            menu.click(player);

            new FoliaRunnable(this.plugin.getServer().getGlobalRegionScheduler()) {
                @Override
                public void run() {
                    inventory.setItem(slot, itemStack);
                }
            }.runDelayed(this.plugin, 3 * 20);

            return;
        }

        final long price = auction.getPrice();

        if (price == 0L) return;

        final VaultSupport support = this.plugin.getSupport();

        if (support.getMoney(player) < price) {
            String itemName = config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Item");
            String name = config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Name");

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

            if (config.contains("Settings.GUISettings.OtherSettings.Cant-Afford.Lore")) {
                itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings.Cant-Afford.Lore"));
            }

            inventory.setItem(slot, itemBuilder.build());
            menu.click(player);

            new FoliaRunnable(this.plugin.getServer().getGlobalRegionScheduler()) {
                @Override
                public void run() {
                    inventory.setItem(slot, itemStack);
                }
            }.runDelayed(this.plugin, 3 * 20);

            return;
        }

        final String auction_id = auction.getId();

        if (auction.isBiddable()) {
            if (uuid.toString().equalsIgnoreCase(auction.getUuid().toString())) {
                String itemName = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Item");
                String name = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Name");

                ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                if (config.contains("Settings.GUISettings.OtherSettings.Top-Bidder.Lore")) {
                    itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings.Top-Bidder.Lore"));
                }

                inventory.setItem(slot, itemBuilder.build());

                menu.click(player);

                new FoliaRunnable(this.plugin.getServer().getGlobalRegionScheduler()) {
                    @Override
                    public void run() {
                        inventory.setItem(slot, itemStack);
                    }
                }.runDelayed(this.plugin, 3 * 20);

                return;
            }

            menu.click(player);

            GuiManager.openBidding(player, auction_id, auction);

            return;
        }

        menu.click(player);

        GuiManager.openBuying(player, auction_id, auction);
    }

    public void calculateItems() {
        this.userManager.getAuctions().forEach(((uuid, auctions) -> auctions.forEach(auction -> {
            final ItemBuilder itemBuilder = auction.getActiveItem(this.shopType);

            if (this.category != null && this.category != Category.NONE && !this.category.getItems().contains(itemBuilder.getMaterial())) {
                return;
            }

            this.items.add(auction);
        })));

        this.maxPages = getMaxPage(this.items == null ? new ArrayList<>() : this.items);
    }
}