package us.crazycrew.crazyauctions.api.auctionhouse.interfaces;

import us.crazycrew.crazyauctions.api.auctionhouse.enums.AuctionType;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Created: 2/19/2023
 * Time: Unknown
 * Last Edited: 2/28/2023 @ 3:04 AM
 *
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