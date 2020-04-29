package me.badbones69.crazyauctions.api.objects;

import me.badbones69.crazyauctions.api.objects.items.BidItem;
import me.badbones69.crazyauctions.api.objects.items.ExpiredItem;
import me.badbones69.crazyauctions.api.objects.items.SellItem;

import java.util.ArrayList;
import java.util.List;

public class UserItems {
    
    private List<SellItem> sellingItems;
    private List<BidItem> biddingItems;
    private List<ExpiredItem> expiredItems;
    
    public UserItems() {
        sellingItems = new ArrayList<>();
        biddingItems = new ArrayList<>();
        expiredItems = new ArrayList<>();
    }
    
    public UserItems(List<SellItem> sellingItems, List<BidItem> biddingItems, List<ExpiredItem> expiredItems) {
        this.sellingItems = sellingItems;
        this.biddingItems = biddingItems;
        this.expiredItems = expiredItems;
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