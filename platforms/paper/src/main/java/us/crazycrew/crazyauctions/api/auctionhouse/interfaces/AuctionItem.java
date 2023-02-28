package us.crazycrew.crazyauctions.api.auctionhouse.interfaces;

import us.crazycrew.crazyauctions.api.auctionhouse.enums.AuctionType;
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