package com.badbones69.crazyauctions.api.guis.types;

import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.Reasons;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.api.events.AuctionCancelledEvent;
import com.badbones69.crazyauctions.api.guis.Holder;
import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.api.GuiManager;
import com.badbones69.crazyauctions.tasks.objects.Auction;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurrentMenu extends Holder {

    private FileConfiguration config;
    private List<String> options;
    private List<Auction> items;
    private int maxPages;

    public CurrentMenu(final Player player, final String title, final int size, final int page) {
        super(player, title, size, page);

        this.config = Files.config.getConfiguration();
        this.options = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public CurrentMenu() {}

    @Override
    public final Holder build() {
        this.options.addAll(List.of(
                "Back",
                "PreviousPage",
                "NextPage",
                "WhatIsThis.CurrentItems"
        ));

        this.items = this.userManager.getAuctions().get(this.player.getUniqueId());

        this.maxPages = getMaxPage(this.items);

        for (final String key : this.options) {
            if (!this.config.contains("Settings.GUISettings.OtherSettings." + key)) {
                continue;
            }

            if (!this.config.getBoolean("Settings.GUISettings.OtherSettings." + key + ".Toggle", true)) {
                continue;
            }

            String id = this.config.getString("Settings.GUISettings.OtherSettings." + key + ".Item");
            String name = this.config.getString("Settings.GUISettings.OtherSettings." + key + ".Name");
            int slot = this.config.getInt("Settings.GUISettings.OtherSettings." + key + ".Slot");

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).addString(key, Keys.auction_button.getNamespacedKey()).setAmount(1);

            if (this.config.contains("Settings.GUISettings.OtherSettings." + key + ".Lore")) {
                itemBuilder.setLore(this.config.getStringList("Settings.GUISettings.OtherSettings." + key + ".Lore"));
            }

            this.inventory.setItem(slot - 1, itemBuilder.build());
        }

        for (final Auction item : getPageItems(this.items, getPage(), getSize())) {
            int slot = this.inventory.firstEmpty();

            this.inventory.setItem(slot, item.getItemBuilder().build());
        }

        this.player.openInventory(this.inventory);

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof CurrentMenu menu)) return;

        event.setCancelled(true);

        final int slot = event.getSlot();

        final Inventory inventory = menu.getInventory();

        if (slot > inventory.getSize()) return;

        if (event.getCurrentItem() == null) return;

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) return;

        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        final Player player = (Player) event.getWhoClicked();

        if (container.has(Keys.auction_button.getNamespacedKey())) {
            final String type = container.getOrDefault(Keys.auction_button.getNamespacedKey(), PersistentDataType.STRING, "");

            if (!type.isEmpty()) {
                switch (type) {
                    case "NextPage" -> {
                        menu.click(player);

                        if (menu.getPage() >= menu.maxPages) {
                            return;
                        }

                        menu.nextPage();

                        GuiManager.openPlayersCurrentList(player, menu.getPage());

                        return;
                    }

                    case "PreviousPage" -> {
                        menu.click(player);

                        final int page = menu.getPage();

                        if (page <= 1) {
                            return;
                        }

                        menu.backPage();

                        GuiManager.openPlayersCurrentList(player, menu.getPage());

                        return;
                    }

                    case "Back" -> {
                        GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                        menu.click(player);

                        return;
                    }
                }
            }
        }

        player.sendMessage(Messages.CANCELLED_ITEM.getMessage(player));

        final String item = auction.getString("Item");

        AuctionCancelledEvent auctionCancelledEvent = new AuctionCancelledEvent(player, Methods.fromBase64(item), Reasons.PLAYER_FORCE_CANCEL);
        this.plugin.getServer().getPluginManager().callEvent(auctionCancelledEvent);

        int num = 1;
        for (;data.contains("OutOfTime/Cancelled." + num); num++);

        data.set("OutOfTime/Cancelled." + num + ".Seller", auction.getString("Seller"));
        data.set("OutOfTime/Cancelled." + num + ".Full-Time", auction.getString("Full-Time"));
        data.set("OutOfTime/Cancelled." + num + ".StoreID", auction.getString("StoreID"));
        data.set("OutOfTime/Cancelled." + num + ".Item", item);

        data.set("Items." + id, null);

        Files.data.save();

        menu.click(player);

        GuiManager.openPlayersCurrentList(player, 1);
    }
}