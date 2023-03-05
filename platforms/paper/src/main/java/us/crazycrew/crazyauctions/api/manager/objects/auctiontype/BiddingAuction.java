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
 * Description: Creates the bidding auction type
 */
public class BiddingAuction implements AuctionItem {

    AuctionType auctionType = AuctionType.BID;
    UUID seller;
    UUID highestBidder;
    long price;
    long currentBid;
    long expireTime;
    ItemStack sellingItem;

    public BiddingAuction(UUID seller, UUID highestBidder, long price, long currentBid, long expireTime, ItemStack sellingItem) {
        this.seller = seller;
        this.highestBidder = highestBidder;
        this.price = price;
        this.currentBid = 0;
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

    public UUID getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(UUID highestBidder) {
        this.highestBidder = highestBidder;
    }

    @Override
    public long getPrice() {
        return price;
    }

    public long getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(long currentBid) {
        this.currentBid = currentBid;
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