package me.badbones69.crazyauctions.api.objects.items;

import me.badbones69.crazyauctions.api.interfaces.AuctionItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserItems {
    
    private UUID ownerUUID;
    private String name;
    private List<SellItem> sellingItems;
    private List<BidItem> biddingItems;
    private List<ExpiredItem> expiredItems;
    
    public UserItems(UUID ownerUUID, String name) {
        new UserItems(ownerUUID, name, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
    
    public UserItems(UUID ownerUUID, String name, List<SellItem> sellingItems, List<BidItem> biddingItems, List<ExpiredItem> expiredItems) {
        this.ownerUUID = ownerUUID;
        this.name = name;
        this.sellingItems = sellingItems;
        this.biddingItems = biddingItems;
        this.expiredItems = expiredItems;
    }
    
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    
    public String getName() {
        return name;
    }
    
    public void addAuctionItem(AuctionItem auctionItem) {
        if (auctionItem instanceof SellItem) {
            sellingItems.add((SellItem) auctionItem);
        } else if (auctionItem instanceof BidItem) {
            biddingItems.add((BidItem) auctionItem);
        } else {
            expiredItems.add((ExpiredItem) auctionItem);
        }
    }
    
    public void removeAuctionItem(AuctionItem auctionItem) {
        if (auctionItem instanceof SellItem) {
            sellingItems.remove(auctionItem);
        } else if (auctionItem instanceof BidItem) {
            biddingItems.remove(auctionItem);
        } else {
            expiredItems.remove(auctionItem);
        }
    }
    
    public List<SellItem> getSellingItems() {
        return sellingItems;
    }
    
    public List<BidItem> getBiddingItems() {
        return biddingItems;
    }
    
    public List<ExpiredItem> getExpiredItems() {
        return expiredItems;
    }
    
}