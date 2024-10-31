package com.badbones69.crazyauctions.api.guis.types;

import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.api.guis.Holder;
import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.api.GuiManager;
import com.badbones69.crazyauctions.tasks.objects.AuctionItem;
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

public class CurrentMenu extends Holder {

    private FileConfiguration config;
    private List<String> options;
    private List<AuctionItem> items;
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

        calculateItems();

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

        for (final AuctionItem item : getPageItems(this.items, getPage(), getSize())) {
            int slot = this.inventory.firstEmpty();

            this.inventory.setItem(slot, item.getCurrentItem().build());
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

        final UUID uuid = player.getUniqueId();

        final AuctionItem auction = this.userManager.getAuctionItemById(uuid, container.getOrDefault(Keys.auction_store_id.getNamespacedKey(), PersistentDataType.STRING, ""));

        if (auction == null) return;

        this.userManager.addExpiredItem(player, auction);

        player.sendMessage(Messages.CANCELLED_ITEM.getMessage(player));

        menu.click(player);

        GuiManager.openPlayersCurrentList(player, menu.getPage());
    }

    public void calculateItems() {
        final UUID uuid = this.player.getUniqueId();

        if (this.userManager.hasAuction(uuid)) {
            this.items = this.userManager.getAuctions(uuid);
        }

        this.maxPages = getMaxPage(this.items);
    }
}