package com.badbones69.crazyauctions.api.guis.types;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.api.guis.Holder;
import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.api.GuiManager;
import com.badbones69.crazyauctions.tasks.InventoryManager;
import com.badbones69.crazyauctions.tasks.objects.ExpiredItem;
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

public class ExpiredMenu extends Holder {

    private List<ExpiredItem> items;
    private List<String> options;

    private FileConfiguration config;
    private int maxPages;

    public ExpiredMenu(final Player player, final String title, final int size, final int page) {
        super(player, title, size, page);

        this.items = new ArrayList<>();
        this.options = new ArrayList<>();

        this.config = Files.config.getConfiguration();
    }

    public ExpiredMenu() {}

    @Override
    public final Holder build() {
        this.options.addAll(List.of(
                "Back",
                "PreviousPage",
                "Return",
                "NextPage",
                "WhatIsThis.Cancelled/ExpiredItems"
        ));

        this.items = this.userManager.getExpiredItems().get(this.player.getUniqueId());

        this.maxPages = getExpiredMaxPages(this.items);

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

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (this.config.contains("Settings.GUISettings.OtherSettings." + key + ".Lore")) {
                itemBuilder.setLore(this.config.getStringList("Settings.GUISettings.OtherSettings." + key + ".Lore"));
            }

            switch (key) {
                case "NextPage" -> this.inventory.setItem(slot - 1, InventoryManager.getNextButton(this.player, this).build());

                case "PreviousPage" -> this.inventory.setItem(slot - 1, InventoryManager.getBackButton(this.player, this).build());

                default -> this.inventory.setItem(slot - 1, itemBuilder.addString(key, Keys.auction_button.getNamespacedKey()).build());
            }
        }

        for (final ExpiredItem item : getPageItem(this.items, getPage(), getSize())) {
            int slot = this.inventory.firstEmpty();

            this.inventory.setItem(slot, item.getExpiredItem().build());
        }

        this.player.openInventory(this.inventory);

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof ExpiredMenu menu)) return;

        event.setCancelled(true);

        final int slot = event.getSlot();

        final Inventory inventory = menu.getInventory();

        if (slot > inventory.getSize()) return;

        if (event.getCurrentItem() == null) return;

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) return;

        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        if (!container.has(Keys.auction_button.getNamespacedKey())) return;

        final String type = container.getOrDefault(Keys.auction_button.getNamespacedKey(), PersistentDataType.STRING, "");

        if (type.isEmpty()) return;

        final Player player = (Player) event.getWhoClicked();

        switch (type) {
            case "Back" -> {
                menu.click(player);

                GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                return;
            }

            case "PreviousPage" -> {
                menu.click(player);

                final int page = menu.getPage();

                if (page > 1 && page <= menu.maxPages) {
                    return;
                }

                menu.backPage();

                GuiManager.openPlayersExpiredList(player, menu.getPage());

                return;
            }

            case "NextPage" -> {
                menu.click(player);

                if (menu.getPage() >= menu.maxPages) {
                    return;
                }

                menu.nextPage();

                GuiManager.openPlayersExpiredList(player, menu.getPage());

                return;
            }

            case "Return" -> {
                if (Methods.isInvFull(player)) { // run this first obviously, just because
                    player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

                    return;
                }

                final FileConfiguration data = Files.data.getConfiguration();

                final ConfigurationSection section = data.getConfigurationSection("expired_auctions");

                if (section == null) return;

                final ConfigurationSection player_section = section.getConfigurationSection(player.getUniqueId().toString());

                if (player_section == null) return;

                final Inventory player_inventory = player.getInventory();

                for (final String key : section.getKeys(false)) {
                    if (Methods.isInvFull(player)) { // run this here obviously as well
                        player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

                        break;
                    }

                    final ConfigurationSection auction_section = section.getConfigurationSection(key);

                    if (auction_section == null) continue;

                    final ItemStack auction_item = Methods.fromBase64(auction_section.getString("item"));

                    if (auction_item == null) continue;

                    player_inventory.addItem(auction_item);
                }

                this.userManager.removeExpiredItems(this.player);

                Files.data.save();

                player.sendMessage(Messages.GOT_ITEM_BACK.getMessage(player));

                menu.click(player);

                GuiManager.openPlayersExpiredList(player, menu.getPage());

                return;
            }
        }

        if (Methods.isInvFull(player)) { // run this here obviously as well
            player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

            return;
        }

        final UUID uuid = player.getUniqueId();

        final ExpiredItem auction = this.userManager.getExpiredItemById(uuid, container.getOrDefault(Keys.auction_store_id.getNamespacedKey(), PersistentDataType.STRING, ""));

        if (auction == null) return;

        player.getInventory().addItem(auction.asItemStack());

        this.userManager.removeExpiredItem(auction);

        Files.data.save();

        menu.click(player);

        GuiManager.openPlayersExpiredList(player, menu.getPage());
    }
}