package com.badbones69.crazyauctions.api.manager.objects;

import com.badbones69.crazyauctions.api.manager.enums.AuctionType;
import com.badbones69.crazyauctions.api.manager.interfaces.AuctionItem;
import com.badbones69.crazyauctions.api.manager.objects.auctiontype.BiddingAuction;
import com.badbones69.crazyauctions.api.manager.objects.auctiontype.SellingAuction;
import com.badbones69.crazyauctions.api.events.AuctionAddEvent;
import com.badbones69.crazyauctions.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Description: Creates the main auction house menu
 */
public class AuctionHouse {

    private String name;
    private FileConfiguration auctionFile;
    private InventorySettings inventorySettings;
    private List<AuctionItem> auctionItems = new ArrayList<>();
    private List<AuctionCategory> auctionCategories = new ArrayList<>();

    public AuctionHouse(FileConfiguration file) {
        this.name = file.getString("auction-house.settings.name");
        this.auctionFile = file;
        this.inventorySettings = new InventorySettings(file);
        //Loads the auction house listings into the auction house.
        //TODO this needs to be moved to a seperated data file that doesnt hold all the auction house settings.
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
        //Loads the category items into the auction house.
        //TODO Need to add the default categories like isPotion, isArmor, isFood, Ect...
        //for (String category : file.getConfigurationSection("auction-house.categories").getKeys(false)) {
        //    String path = "auction-house.categories." + category + ".";
        //    auctionCategories.add(new AuctionCategory(
        //    category,
        //    file.getInt(path + "slot"),
            //ItemUtils.convertString(file.getString(path + "item")),
            //file.getStringList(path + "items").stream().map(Material :: matchMaterial).collect(Collectors.toList())));
        //}
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

    public long getAuctionCount(AuctionType auctionType) {
        return auctionItems.stream().filter(auctionItem -> auctionType == auctionItem.getAuctionType()).count();
    }

    public void addAuctionItem(AuctionItem auctionItem) {
        auctionItems.add(auctionItem);
        AuctionAddEvent event = new AuctionAddEvent(auctionItem.getSeller(), this, auctionItem);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }
}