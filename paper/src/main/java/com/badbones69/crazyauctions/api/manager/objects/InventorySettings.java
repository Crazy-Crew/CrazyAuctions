package com.badbones69.crazyauctions.api.manager.objects;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Description: Creates the auction house inventory settings
 */
public class InventorySettings {

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