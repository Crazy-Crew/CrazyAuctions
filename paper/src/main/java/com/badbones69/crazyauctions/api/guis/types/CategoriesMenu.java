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
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.List;

public class CategoriesMenu extends Holder {

    private List<String> options;

    private FileConfiguration config;

    public CategoriesMenu(final Player player, final ShopType shopType, final String title, final int size) {
        super(player, shopType, title, size);

        this.options = new ArrayList<>();

        this.config = Files.config.getConfiguration();
    }

    public CategoriesMenu() {}

    @Override
    public final Holder build() {
        Methods.updateAuction();

        this.options.addAll(List.of(
                "OtherSettings.Back",
                "OtherSettings.WhatIsThis.Categories",
                "Category-Settings.Armor",
                "Category-Settings.Weapons",
                "Category-Settings.Tools",
                "Category-Settings.Food",
                "Category-Settings.Potions",
                "Category-Settings.Blocks",
                "Category-Settings.Other",
                "Category-Settings.None"
        ));

        for (final String key : this.options) {
            if (!this.config.contains("Settings.GUISettings." + key)) {
                continue;
            }

            if (!this.config.getBoolean("Settings.GUISettings." + key + ".Toggle", true)) {
                continue;
            }

            String id = this.config.getString("Settings.GUISettings." + key + ".Item");
            String name = this.config.getString("Settings.GUISettings." + key + ".Name");
            int slot = this.config.getInt("Settings.GUISettings." + key + ".Slot");

            ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).addString(key).setAmount(1);

            if (this.config.contains("Settings.GUISettings." + key + ".Lore")) {
                itemBuilder.setLore(this.config.getStringList("Settings.GUISettings." + key + ".Lore"));
            }

            this.inventory.setItem(slot - 1, itemBuilder.build());
        }

        HolderManager.addShopType(this.player, this.shopType);

        this.player.openInventory(this.inventory);

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof CategoriesMenu menu)) return;

        event.setCancelled(true);

        final int slot = event.getSlot();

        final Inventory inventory = menu.getInventory();

        if (slot > inventory.getSize()) return;

        if (event.getCurrentItem() == null) return;

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) return;

        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        if (!container.has(Keys.auction_button.getNamespacedKey())) return;

        final Player player = (Player) event.getWhoClicked();

        final String type = container.getOrDefault(Keys.auction_button.getNamespacedKey(), PersistentDataType.STRING, "Category-Settings.None");

        final Category category = Category.getFromName(type);

        GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player) != null ? HolderManager.getShopCategory(player) : category, 1);

        click();
    }
}