package com.badbones69.crazyauctions.api.auctionhouse.objects;

import com.badbones69.crazyauctions.api.auctionhouse.enums.AuctionType;
import com.badbones69.crazyauctions.api.auctionhouse.interfaces.AuctionItem;
import com.badbones69.crazyauctions.api.auctionhouse.objects.auctiontype.BiddingAuction;
import com.badbones69.crazyauctions.api.auctionhouse.objects.auctiontype.SellingAuction;
import com.badbones69.crazyauctions.api.events.AuctionAddEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionHouse {

    private String name;
    private FileConfiguration auctionFile;
    private InventorySettings inventorySettings;
    private List<AuctionItem> auctionItems = new ArrayList<>();

    public AuctionHouse(FileConfiguration file) {
        this.name = file.getString("auction-house.settings.name");
        this.auctionFile = file;
        this.inventorySettings = new InventorySettings(file);
        for (String auctionID : file.getConfigurationSection("auction-house.item-on-auction").getKeys(false)) {
            String path = "auction-house.item-on-auction" + auctionID + ".";
            AuctionType auctionType = AuctionType.getTypeFromName(file.getString(path + "auction-type"));
            if (auctionType == AuctionType.SELL) {
                auctionItems.add(new SellingAuction(
                UUID.fromString(file.getString(path + "seller-uuid")),
                file.getLong(path + "price"),
                file.getLong(path + "expire-time"),
                file.getItemStack(path + "selling-item")));
            } else {
                auctionItems.add(new BiddingAuction(
                UUID.fromString(file.getString(path + "seller-uuid")),
                UUID.fromString(file.getString(path + "highest-bidder-uuid")),
                file.getLong(path + "price"),
                file.getLong(path + "current-bid"),
                file.getLong(path + "expire-time"),
                file.getItemStack(path + "selling-item")));
            }
        }
    }

    public String getName() {
        return name;
    }

    public FileConfiguration getAuctionFile() {
        return auctionFile;
    }

    public InventorySettings getInventorySettings() {
        return inventorySettings;
    }

    public List<AuctionItem> getAuctionItems() {
        return auctionItems;
    }

    public void addAuctionItem(AuctionItem auctionItem) {
        auctionItems.add(auctionItem);
        AuctionAddEvent event = new AuctionAddEvent(auctionItem.getSeller(), this, auctionItem);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

}