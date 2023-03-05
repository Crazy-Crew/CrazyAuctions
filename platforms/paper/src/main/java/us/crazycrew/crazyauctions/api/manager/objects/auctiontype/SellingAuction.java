package us.crazycrew.crazyauctions.api.manager.objects.auctiontype;

import us.crazycrew.crazyauctions.api.manager.enums.AuctionType;
import us.crazycrew.crazyauctions.api.manager.interfaces.AuctionItem;
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
 * Description: Creates the selling auction type
 */
public class SellingAuction implements AuctionItem {

    AuctionType auctionType = AuctionType.SELL;
    UUID seller;
    long price;
    long expireTime;
    ItemStack sellingItem;

    public SellingAuction(UUID seller, long price, long expireTime, ItemStack sellingItem) {
        this.seller = seller;
        this.price = price;
        this.expireTime = expireTime;
        this.sellingItem = sellingItem;
    }

    @Override
    public AuctionType getAuctionType() {
        return auctionType;
    }

    @Override
    public UUID getSeller() {
        return seller;
    }

    @Override
    public long getPrice() {
        return price;
    }

    @Override
    public long getExpireTime() {
        return expireTime;
    }

    @Override
    public ItemStack getSellingItem() {
        return sellingItem;
    }
}