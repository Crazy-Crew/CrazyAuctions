package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AuctionMenu implements InventoryHolder {

    protected final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private final Component title;
    private final int size;
    private final Inventory inventory;

    public  AuctionMenu(int size, Component title) {
        this.title = title;
        this.size = size;

        this.inventory = this.plugin.getServer().createInventory(this, this.size, title);
    }

    public  AuctionMenu(int size, String stringTitle) {
        this.title = Methods.legacyTranslateColourCodes(stringTitle);
        this.size = size;

        this.inventory = this.plugin.getServer().createInventory(this, this.size, title);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

}
