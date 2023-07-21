package com.badbones69.crazyauctions.commands.inventories;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AuctionInventory implements InventoryHolder {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private final Inventory inventory;

    public AuctionInventory() {
        this.inventory = plugin.getServer().createInventory(this, 9);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}