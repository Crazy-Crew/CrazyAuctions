package me.badbones69.crazyauctions.api.objects;

import me.badbones69.crazyauctions.api.objects.items.BidItem;
import me.badbones69.crazyauctions.api.objects.items.ExpiredItem;
import me.badbones69.crazyauctions.api.objects.items.SellItem;

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
        this.ownerUUID = ownerUUID;
        this.name = name;
        sellingItems = new ArrayList<>();
        biddingItems = new ArrayList<>();
        expiredItems = new ArrayList<>();
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
    
    public void addSellItem(SellItem item) {
        sellingItems.add(item);
    }
    
    public List<SellItem> getSellingItems() {
        return sellingItems;
    }
    
    public void addBiddingItem(BidItem item) {
        biddingItems.add(item);
    }
    
    public List<BidItem> getBiddingItems() {
        return biddingItems;
    }
    
    public void addExpiredItem(ExpiredItem item) {
        expiredItems.add(item);
    }
    
    public List<ExpiredItem> getExpiredItems() {
        return expiredItems;
    }
    
}