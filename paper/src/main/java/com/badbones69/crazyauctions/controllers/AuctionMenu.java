package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.CrazyAuctions;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AuctionMenu implements InventoryHolder {

    protected final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);
    protected final Server server = this.plugin.getServer();
    protected final ComponentLogger logger = this.plugin.getComponentLogger();

    private final String title;
    private final Inventory inventory;
    private final int pageNumber;

    public AuctionMenu(int size, String title) {
        this.title = title.replaceAll("§", "&");
        this.pageNumber = 1;
        this.inventory = this.server.createInventory(this, size, title);
    }

    public AuctionMenu(int size, String title, int page) {
        this.title = title.replaceAll("§", "&");
        this.pageNumber = page;
        this.inventory = this.server.createInventory(this, size, title);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public String getTitle() {
        return this.title;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }
}