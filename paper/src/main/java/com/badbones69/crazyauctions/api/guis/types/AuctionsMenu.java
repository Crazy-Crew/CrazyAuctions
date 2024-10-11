package com.badbones69.crazyauctions.api.guis.types;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.Reasons;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.api.events.AuctionCancelledEvent;
import com.badbones69.crazyauctions.api.guis.Holder;
import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import static com.badbones69.crazyauctions.controllers.GuiListener.openCategories;
import static com.badbones69.crazyauctions.controllers.GuiListener.openPlayersCurrentList;
import static com.badbones69.crazyauctions.controllers.GuiListener.openPlayersExpiredList;

@SuppressWarnings({"FieldCanBeLocal", "UnusedAssignment"})
public class AuctionsMenu extends Holder {

    private List<ItemStack> items;
    private List<String> options;
    private List<Integer> ids;

    private FileConfiguration config;
    private FileConfiguration data;

    private Category category;

    public AuctionsMenu(final Player player, final ShopType shopType, final Category category, final String title, final int size, final int page) {
        super(player, shopType, title, size, page);

        this.items = new ArrayList<>();
        this.options = new ArrayList<>();
        this.ids = new ArrayList<>();

        this.config = Files.config.getConfiguration();
        this.data = Files.data.getConfiguration();

        if (!this.data.contains("Items")) {
            this.data.set("Items.Clear", null);

            Files.data.save();
        }

        HolderManager.addShopCategory(player, this.category = category);
    }

    public AuctionsMenu() {}

    @Override
    public final Holder build() {
        Methods.updateAuction();

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

        getItems(); // populates the lists

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

                this.inventory.setItem(slot - 1, itemBuilder.setLore(lore).addString(key).build());
            } else {
                this.inventory.setItem(slot - 1, itemBuilder.setLore(lore).addString(key).build());
            }
        }

        for (final ItemStack item : Methods.getPage(this.items, this.page)) {
            int slot = this.inventory.firstEmpty();

            this.inventory.setItem(slot, item);
        }

        HolderManager.addPages(this.player, Methods.getPageInts(this.ids, this.page));

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
        FileConfiguration data = Files.data.getConfiguration();

        if (container.has(Keys.auction_button.getNamespacedKey())) {
            click();

            String type = container.getOrDefault(Keys.auction_button.getNamespacedKey(), PersistentDataType.STRING, "Refresh");

            final String title = event.getView().getTitle();

            switch (type) {
                case "NextPage", "Your-Item", "Top-Bidder", "Cant-Afford" -> {
                    return;
                }

                case "PreviousPage" -> {
                    Methods.updateAuction();

                    int page = Integer.parseInt(title.split("#")[1]);

                    if (page == 1) page++;

                    GuiListener.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), page - 1);

                    return;
                }

                case "Refesh", "Refresh" -> {
                    Methods.updateAuction();

                    int page = Integer.parseInt(title.split("#")[1]);

                    GuiListener.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), page);

                    return;
                }

                case "Bidding/Selling.Selling" -> {
                    GuiListener.openShop(player, ShopType.BID, HolderManager.getShopCategory(player), 1);

                    return;
                }

                case "Bidding/Selling.Bidding" -> {
                    GuiListener.openShop(player, ShopType.SELL, HolderManager.getShopCategory(player), 1);

                    return;
                }

                case "Cancelled/ExpiredItems" -> {
                    openPlayersExpiredList(player, 1);

                    return;
                }

                case "SellingItems" -> {
                    openPlayersCurrentList(player, 1);

                    return;
                }

                case "Category1", "Category2" -> {
                    openCategories(player, HolderManager.getShopType(player));

                    return;
                }
            }
        }

        if (!HolderManager.containsPage(player)) return;

        if (!data.contains("Items")) return;

        final ConfigurationSection section = data.getConfigurationSection("Items");

        if (section == null) return;

        final List<Integer> pages = HolderManager.getPages(player);

        if (pages.size() < slot) return;

        final int id = pages.get(slot);

        final UUID uuid = player.getUniqueId();

        for (String i : section.getKeys(false)) {
            int ID = data.getInt("Items." + i + ".StoreID");

            if (id != ID) {
                return;
            }

            if (player.hasPermission("crazyauctions.admin") || player.hasPermission("crazyauctions.force-end")) {
                if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    int num = 1;

                    for (;data.contains("OutOfTime/Cancelled." + num); num++);

                    String seller = data.getString("Items." + i + ".Seller");

                    Player sellerPlayer = Methods.getPlayer(seller);

                    if (Methods.isOnline(seller) && sellerPlayer != null) {
                        sellerPlayer.sendMessage(Messages.ADMIN_FORCE_CANCELLED_TO_PLAYER.getMessage(player));
                    }

                    AuctionCancelledEvent auctionCancelledEvent = new AuctionCancelledEvent((sellerPlayer != null ? sellerPlayer : Methods.getOfflinePlayer(seller)), Methods.fromBase64(data.getString("Items." + ID + ".Item")), Reasons.ADMIN_FORCE_CANCEL);
                    this.server.getPluginManager().callEvent(auctionCancelledEvent);

                    data.set("OutOfTime/Cancelled." + num + ".Seller", section.getString("Seller"));
                    data.set("OutOfTime/Cancelled." + num + ".Full-Time", section.getLong("Full-Time"));
                    data.set("OutOfTime/Cancelled." + num + ".StoreID", section.getInt("StoreID"));
                    data.set("OutOfTime/Cancelled." + num + ".Item", data.getString("Items." + ID + ".Item"));
                    data.set("Items." + i, null);

                    Files.data.save();

                    player.sendMessage(Messages.ADMIN_FORCE_CANCELLED.getMessage(player));

                    click();

                    int page = Integer.parseInt(event.getView().getTitle().split("#")[1]);

                    GuiListener.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), page);

                    return;
                }
            }

            if (data.getString("Items." + i + ".Seller", "").equalsIgnoreCase(uuid.toString())) {
                String itemName = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Item");
                String name = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Name");

                ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                if (config.contains("Settings.GUISettings.OtherSettings.Your-Item.Lore")) {
                    itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings.Your-Item.Lore"));
                }

                inventory.setItem(slot, itemBuilder.build());

                click();

                new FoliaRunnable(this.plugin.getServer().getGlobalRegionScheduler()) {
                    @Override
                    public void run() {
                        inventory.setItem(slot, itemStack);
                    }
                }.runDelayed(this.plugin, 3 * 20);

                return;
            }

            long cost = data.getLong("Items." + i + ".Price");

            if (this.plugin.getSupport().getMoney(player) < cost) {
                String itemName = config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Item");
                String name = config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Name");

                ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                if (config.contains("Settings.GUISettings.OtherSettings.Cant-Afford.Lore")) {
                    itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings.Cant-Afford.Lore"));
                }

                inventory.setItem(slot, itemBuilder.build());
                click();

                new FoliaRunnable(this.plugin.getServer().getGlobalRegionScheduler()) {
                    @Override
                    public void run() {
                        inventory.setItem(slot, itemStack);
                    }
                }.runDelayed(this.plugin, 3 * 20);

                return;
            }

            if (data.getBoolean("Items." + i + ".Biddable")) {
                if (player.getUniqueId().toString().equalsIgnoreCase(data.getString("Items." + i + ".TopBidder"))) {
                    String itemName = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Item");
                    String name = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Name");

                    ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                    if (config.contains("Settings.GUISettings.OtherSettings.Top-Bidder.Lore")) {
                        itemBuilder.setLore(config.getStringList("Settings.GUISettings.OtherSettings.Top-Bidder.Lore"));
                    }

                    inventory.setItem(slot, itemBuilder.build());

                    click();

                    new FoliaRunnable(this.plugin.getServer().getGlobalRegionScheduler()) {
                        @Override
                        public void run() {
                            inventory.setItem(slot, itemStack);
                        }
                    }.runDelayed(this.plugin, 3 * 20);

                    return;
                }

                click();

                GuiListener.openBidding(player, i);

                HolderManager.addBidId(player, i);
            } else {
                click();

                GuiListener.openBuying(player, i);
            }
        }
    }

    private void getItems() {
        final ConfigurationSection section = this.data.getConfigurationSection("Items");

        if (section == null) return;

        for (String key : section.getKeys(false)) {
            final ConfigurationSection auction = section.getConfigurationSection(key);

            if (auction == null) continue;

            final String item = auction.getString("Item", "");

            if (item.isEmpty()) continue;

            final ItemBuilder itemBuilder = ItemBuilder.convertItemStack(item);

            if (this.category != Category.NONE && !this.category.getItems().contains(itemBuilder.getMaterial())) continue;

            final String seller = auction.getString("Seller", "");

            if (seller.isEmpty()) continue;

            final long price = auction.getLong("Price");

            final String priceFormat = String.format(Locale.ENGLISH, "%,d", price);

            final OfflinePlayer player = Methods.getOfflinePlayer(seller);

            final String time = Methods.convertToTime(auction.getLong("Time-Till-Expire"));

            final List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

            lore.add(" ");

            if (this.shopType == ShopType.BID && auction.getBoolean("Biddable")) {
                final String bidder = auction.getString("TopBidder", "None");

                final OfflinePlayer top_bidder = bidder.equalsIgnoreCase("None") ? null : Methods.getOfflinePlayer(bidder);

                for (final String line : this.config.getStringList("Settings.GUISettings.Bidding")) {
                    String newLine = line.replace("%TopBid%", priceFormat).replace("%topbid%", priceFormat);

                    final String targetName = player.getName() == null ? "N/A" : player.getName();

                    newLine = line.replace("%Seller%", targetName).replace("%seller%", targetName);

                    final String bidderName = top_bidder == null ? "N/A" : top_bidder.getName() == null ? "N/A" : top_bidder.getName();

                    newLine = line.replace("%TopBidder%", bidderName).replace("%topbid%", bidderName);

                    lore.add(newLine.replace("%Time%", time).replace("%time%", time).replace("%price%", priceFormat).replace("%Price%", priceFormat));
                }
            }

            if (this.shopType == ShopType.SELL) {
                for (final String line : this.config.getStringList("Settings.GUISettings.SellingItemLore")) {
                    String newLine = line.replace("%TopBid%", priceFormat).replace("%topbid%", priceFormat);

                    final String targetName = player.getName() == null ? "N/A" : player.getName();

                    newLine = line.replace("%Seller%", targetName).replace("%seller%", targetName);

                    lore.add(newLine.replace("%Time%", time).replace("%time%", time).replace("%price%", priceFormat).replace("%Price%", priceFormat));
                }
            }

            itemBuilder.setLore(lore);

            this.items.add(itemBuilder.build());

            this.ids.add(auction.getInt("StoreID"));
        }
    }
}