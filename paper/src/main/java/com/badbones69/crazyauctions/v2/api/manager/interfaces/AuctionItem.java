package com.badbones69.crazyauctions.api.manager.interfaces;

import com.badbones69.crazyauctions.api.manager.enums.AuctionType;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public interface AuctionItem {

    UUID auctionID = UUID.randomUUID();

    default UUID getAuctionID() {
        return auctionID;
    }

    AuctionType getAuctionType();

    UUID getSeller();

    long getPrice();

    long getExpireTime();

    ItemStack getSellingItem();

}