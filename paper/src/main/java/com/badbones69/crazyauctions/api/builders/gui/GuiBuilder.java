package com.badbones69.crazyauctions.api.builders.gui;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.CrazyAuctionsPaper;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import java.util.Map;

@NullMarked
public final class GuiBuilder implements InventoryHolder {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);
    private final CrazyAuctionsPaper platform = this.plugin.getPlatform();
    private final FusionPaper fusion = this.platform.getFusion();
    private final Server server = this.plugin.getServer();

    private final Inventory inventory;
    private final int pageNumber;
    private final String title;

    public GuiBuilder(final int size, final String title, final int pageNumber) {
        this.inventory = this.server.createInventory(this, size, Methods.color(this.fusion.replacePlaceholders(this.title = title.replace("§", "&"), Map.of(
                "{page}", String.valueOf(pageNumber)
        ))));

        this.pageNumber = pageNumber;
    }

    public GuiBuilder(final int size, final String title) {
        this(size, title, 1);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public String getTitle() {
        return this.title;
    }
}