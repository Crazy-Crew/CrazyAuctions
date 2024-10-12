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
import java.util.Locale;
import java.util.UUID;

public class ExpiredMenu extends Holder {

    private List<ItemStack> items;
    private List<String> options;

    private FileConfiguration config;
    private FileConfiguration data;

    private int maxPages;

    public ExpiredMenu(final Player player, final String title, final int size, final int page) {
        super(player, title, size, page);

        this.items = new ArrayList<>();
        this.options = new ArrayList<>();

        this.config = Files.config.getConfiguration();
        this.data = Files.data.getConfiguration();
    }

    public ExpiredMenu() {}

    @Override
    public final Holder build() {
        Methods.updateAuction();

        this.options.addAll(List.of(
                "Back",
                "PreviousPage",
                "Return",
                "NextPage",
                "WhatIsThis.Cancelled/ExpiredItems"
        ));

        getItems();

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

        for (final ItemStack item : getPageItems(this.items, getPage(), getSize())) {
            int slot = this.inventory.firstEmpty();

            this.inventory.setItem(slot, item);
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
                FileConfiguration data = Files.data.getConfiguration();

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

                menu.click(player);

                GuiManager.openPlayersExpiredList(player, menu.getPage());

                return;
            }
        }

        final List<Integer> pages = new ArrayList<>();

        if (pages.size() >= slot) {
            int id = pages.get(slot);

            boolean valid = false;

            final FileConfiguration data = Files.data.getConfiguration();

            final ConfigurationSection section = this.data.getConfigurationSection("OutOfTime/Cancelled");

            if (section != null) {
                for (String key : section.getKeys(false)) {
                    final ConfigurationSection auction = section.getConfigurationSection(key);

                    if (auction == null) continue;

                    int config_id = auction.getInt("StoreID");

                    if (id == config_id) {
                        if (Methods.isInvFull(player)) {
                            player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

                            return;
                        }

                        player.sendMessage(Messages.GOT_ITEM_BACK.getMessage(player));

                        player.getInventory().addItem(Methods.fromBase64(auction.getString("Item")));

                        data.set("OutOfTime/Cancelled." + key, null);

                        Files.data.save();

                        menu.click(player);

                        GuiManager.openPlayersExpiredList(player, 1);

                        return;
                    }
                }
            }

            if (!valid) {
                menu.click(player);

                GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));
            }
        }
    }

    private void getItems() {
        final ConfigurationSection section = this.data.getConfigurationSection("OutOfTime/Cancelled");

        if (section == null) return;

        final UUID uuid = this.player.getUniqueId();

        for (String key : section.getKeys(false)) {
            final ConfigurationSection auction = section.getConfigurationSection(key);

            if (auction == null) continue;

            final String seller = auction.getString("Seller", "");

            if (seller.isEmpty()) continue;

            if (!seller.equalsIgnoreCase(uuid.toString())) continue;

            final String item = auction.getString("Item", "");

            if (item.isEmpty()) continue;

            final ItemBuilder itemBuilder = ItemBuilder.convertItemStack(item);

            final long price = auction.getLong("Price");

            final String priceFormat = String.format(Locale.ENGLISH, "%,d", price);

            final String time = Methods.convertToTime(auction.getLong("Time-Till-Expire"));

            final List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

            lore.add(" ");

            for (final String line : this.config.getStringList("Settings.GUISettings.Cancelled/ExpiredLore")) {
                lore.add(line.replace("%Time%", time).replace("%time%", time).replace("%price%", priceFormat).replace("%Price%", priceFormat));
            }

            itemBuilder.setLore(lore);

            itemBuilder.addInteger(auction.getInt("StoreID"), Keys.auction_id.getNamespacedKey());
            itemBuilder.addString(auction.getName(), Keys.auction_item.getNamespacedKey());

            this.items.add(itemBuilder.build());
        }
    }
}