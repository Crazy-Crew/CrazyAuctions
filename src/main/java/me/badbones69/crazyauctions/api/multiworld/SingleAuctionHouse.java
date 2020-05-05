package me.badbones69.crazyauctions.api.multiworld;

import me.badbones69.crazyauctions.api.enums.ShopType;
import me.badbones69.crazyauctions.api.interfaces.AuctionItem;
import me.badbones69.crazyauctions.api.managers.AuctionManager;
import me.badbones69.crazyauctions.api.objects.gui.AuctionHouse;
import me.badbones69.crazyauctions.api.objects.items.BidItem;
import me.badbones69.crazyauctions.api.objects.items.ExpiredItem;
import me.badbones69.crazyauctions.api.objects.items.SellItem;

import java.util.ArrayList;
import java.util.List;

public class SingleAuctionHouse {
    
    private static SingleAuctionHouse instance = new SingleAuctionHouse();
    private List<AuctionHouse> sellingAuctionHouses = new ArrayList<>();
    private List<AuctionHouse> biddingAuctionHouses = new ArrayList<>();
    private List<SellItem> sellingItems = new ArrayList<>();
    private List<BidItem> biddingItems = new ArrayList<>();
    private List<ExpiredItem> expiredItems = new ArrayList<>();
    private List<AuctionItem> auctionItems = new ArrayList<>();
    private AuctionManager auctionManager = AuctionManager.getInstance();
    
    public static SingleAuctionHouse getInstance() {
        return instance;
    }
    
    public void startUpAuctionHouses() {
        if (sellingAuctionHouses.isEmpty()) {
            for (int page = 1; page <= auctionManager.getMaxPage(new ArrayList<>(sellingItems)); page++) {
                addAuctionHouse(auctionManager.createAuctionHouse(ShopType.SELL, sellingItems, new ArrayList<>()));
            }
        }
        if (biddingAuctionHouses.isEmpty()) {
            for (int page = 1; page <= auctionManager.getMaxPage(new ArrayList<>(biddingItems)); page++) {
                addAuctionHouse(auctionManager.createAuctionHouse(ShopType.BID, new ArrayList<>(), biddingItems));
            }
        }
    }
    
    public void addAuctionHouse(AuctionHouse auctionHouse) {
        if (auctionHouse.getShopType() == ShopType.SELL) {
            sellingAuctionHouses.add(auctionHouse);
        } else {
            biddingAuctionHouses.add(auctionHouse);
        }
    }
    
    public AuctionHouse getAuctionHouse(ShopType shopType) {
        return getAuctionHouse(shopType, 1);
    }
    
    public AuctionHouse getAuctionHouse(ShopType shopType, int page) {
        page = page > 0 ? page - 1 : 0;
        if (shopType == ShopType.SELL) {
            return sellingAuctionHouses.get(page);
        } else {
            return biddingAuctionHouses.get(page);
        }
    }
    
    public void addAuctionItem(AuctionItem auctionItem) {
        if (auctionItem instanceof SellItem) {
            sellingItems.add((SellItem) auctionItem);
        } else if (auctionItem instanceof BidItem) {
            biddingItems.add((BidItem) auctionItem);
        } else {
            expiredItems.add((ExpiredItem) auctionItem);
        }
        auctionItems.add(auctionItem);
    }
    
    public void removeAuctionItem(AuctionItem auctionItem) {
        auctionItems.remove(auctionItem);
        if (auctionItem instanceof SellItem) {
            sellingItems.remove(auctionItem);
        } else if (auctionItem instanceof BidItem) {
            biddingItems.remove(auctionItem);
        } else {
            expiredItems.remove(auctionItem);
        }
    }
    
    public List<AuctionItem> getAuctionItems() {
        return auctionItems;
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