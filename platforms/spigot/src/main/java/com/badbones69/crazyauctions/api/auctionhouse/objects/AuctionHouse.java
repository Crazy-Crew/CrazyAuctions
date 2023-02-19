package com.badbones69.crazyauctions.api.auctionhouse.objects;

import com.badbones69.crazyauctions.api.auctionhouse.enums.AuctionType;
import com.badbones69.crazyauctions.api.auctionhouse.interfaces.AuctionItem;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionHouse {

    private String name;
    private InventorySettings inventorySettings;
    private List<AuctionItem> auctionItems = new ArrayList<>();

    public AuctionHouse(String name, YamlFile file) {
        this.name = name;
        this.inventorySettings = new InventorySettings();
        inventorySettings.setTitle("Set Title Here with Color");
        for (String auctionID : file.getConfigurationSection("auction-house.item-on-auction").getKeys(false)) {
            String path = "auction-house.item-on-auction" + auctionID + ".";
            AuctionType auctionType = AuctionType.getTypeFromName(file.getString(path + "auction-type"));
            if (auctionType == AuctionType.SELL) {
                auctionItems.add(new BuyingAuction(
                UUID.fromString(file.getString(path + "UUID")),
                file.getLong(path + "price"),
                file.getLong(path + "expire-time"),
                file.getI(path + "")
                ));
            } else {

            }
        }
    }

}