package us.crazycrew.crazyauctions.api.manager.interfaces;

import us.crazycrew.crazyauctions.api.manager.enums.AuctionType;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

/**
 * Description: Defines the auction type to use
 */
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