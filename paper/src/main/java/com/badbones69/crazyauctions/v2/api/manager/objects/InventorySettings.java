package com.badbones69.crazyauctions.api.manager.objects;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Description: Creates the auction house inventory settings
 */
public class InventorySettings {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private final String title;
    private final AuctionButtons auctionButtons = null;

    public InventorySettings(FileConfiguration file) {
        String path = "auction-house.settings.";
        this.title = file.getString(path + "inventory-title");
        //this.auctionButtons = new AuctionButtons(file);
    }

    public String getTitle() {
        return title;
    }

    public AuctionButtons getAuctionButtons() {
        return auctionButtons;
    }
}