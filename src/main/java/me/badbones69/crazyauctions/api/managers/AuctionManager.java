package me.badbones69.crazyauctions.api.managers;

import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.objects.TopBidder;
import me.badbones69.crazyauctions.api.objects.UserItems;
import me.badbones69.crazyauctions.api.objects.items.BidItem;
import me.badbones69.crazyauctions.api.objects.items.ExpiredItem;
import me.badbones69.crazyauctions.api.objects.items.SellItem;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AuctionManager {
    
    private static AuctionManager instance = new AuctionManager();
    private boolean isSaving = false;
    private List<SellItem> sellingItems = new ArrayList<>();
    private List<BidItem> biddingItems = new ArrayList<>();
    private List<ExpiredItem> expiredItems = new ArrayList<>();
    private List<UserItems> userItemList = new ArrayList<>();
    
    public void loadAuctionHouse() {
        sellingItems.clear();
        biddingItems.clear();
        expiredItems.clear();
        userItemList.clear();
        //Load Selling, Bidding, and Expired items.
        FileConfiguration data = Files.DATA.getFile();
        if (data.contains("Users")) {
            for (String uuid : data.getConfigurationSection("Users").getKeys(false)) {
                UserItems userItems = getSellingItems(data, "Users." + uuid + ".", uuid);
                userItemList.add(userItems);
                sellingItems.addAll(userItems.getSellingItems());
                biddingItems.addAll(userItems.getBiddingItems());
                expiredItems.addAll(userItems.getExpiredItems());
            }
        }
    }
    
    public void saveAuctionHouse() {
        isSaving = true;
        FileConfiguration data = Files.DATA.getFile();
        //This is done to clear the file of old auctions.
        data.set("Users", null);
        for (UserItems userItem : userItemList) {
            String path = "Users." + userItem.getOwnerUUID() + ".";
            data.set(path + "Name", userItem.getName());
            for (SellItem sellItem : userItem.getSellingItems()) {
                if (!sellItem.isSold()) {
                    if (!sellItem.isExpired()) {
                        String itemPath = path + "Selling." + sellItem.getStoreID() + ".";
                        data.set(itemPath + "Price", sellItem.getPrice());
                        data.set(itemPath + "Expire-Time", sellItem.getExpireTime());
                        data.set(itemPath + "Item", sellItem.getItem());
                    } else {
                        String itemPath = path + "Expired." + sellItem.getStoreID() + ".";
                        ExpiredItem expiredItem = new ExpiredItem(sellItem);
                        data.set(itemPath + "Expire-Time", expiredItem.getExpireTime());
                        data.set(itemPath + "Item", expiredItem.getItem());
                    }
                }
            }
            for (BidItem bidItem : userItem.getBiddingItems()) {
                if (!bidItem.isSold()) {
                    TopBidder topBidder = bidItem.getTopBidder();
                    if (!bidItem.isExpired()) {
                        String itemPath = path + "Bidding." + bidItem.getStoreID() + ".";
                        data.set(itemPath + "Price", bidItem.getPrice());
                        data.set(itemPath + "Expire-Time", bidItem.getExpireTime());
                        data.set(itemPath + "Top-Bidder.UUID", topBidder.getOwnerUUID());
                        data.set(itemPath + "Top-Bidder.Name", topBidder.getOwnerName());
                        data.set(itemPath + "Top-Bidder.Bid", topBidder.getBid());
                        data.set(itemPath + "Item", bidItem.getItem());
                    } else {
                        ExpiredItem expiredItem = new ExpiredItem(bidItem);
                        //TODO make it so it will take the money away from the top bidder since it hasn't been sold yet.
                        if (topBidder.hasBid()) {
                            String itemPath = "Users." + topBidder.getOwnerUUID() + ".Expired." + bidItem.getStoreID() + ".";
                            data.set(itemPath + "Expire-Time", expiredItem.getExpireTime());
                            data.set(itemPath + "Item", expiredItem.getItem());
                        } else {
                            String itemPath = path + "Expired." + bidItem.getStoreID() + ".";
                            data.set(itemPath + "Expire-Time", expiredItem.getExpireTime());
                            data.set(itemPath + "Item", expiredItem.getItem());
                        }
                    }
                }
            }
            for (ExpiredItem expiredItem : userItem.getExpiredItems()) {
                if (!expiredItem.isExpired()) {
                    String itemPath = path + "Selling." + expiredItem.getStoreID() + ".";
                    data.set(itemPath + "Expire-Time", expiredItem.getExpireTime());
                    data.set(itemPath + "Item", expiredItem.getItem());
                }
            }
        }
        Files.DATA.saveFile();
        isSaving = false;
    }
    
    public boolean isAuctionHouseSaving() {
        return isSaving;
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
    
    public static AuctionManager getInstance() {
        return instance;
    }
    
    public UserItems getUserSales(UUID uuid) {
        for (UserItems item : userItemList) {
            if (item.getOwnerUUID().equals(uuid)) {
                return item;
            }
        }
        return null;
    }
    
    private UserItems getSellingItems(FileConfiguration data, String path, String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        String name = data.getString(path + "Name");
        Calendar now = Calendar.getInstance();
        UserItems items = new UserItems(uuid, name);
        if (data.contains(path + "Selling")) {
            for (String storeID : data.getConfigurationSection(path + "Selling").getKeys(false)) {
                String itemPath = path + "Selling." + storeID + ".";
                Calendar expire = Calendar.getInstance();
                expire.setTimeInMillis(data.getLong(itemPath + "Expire-Time"));
                if (now.after(expire)) {
                    items.addExpiredItem(new ExpiredItem(uuid, name, UUID.fromString(storeID), data.getItemStack(itemPath + "Item")));
                } else {
                    items.addSellItem(new SellItem(uuid, name, UUID.fromString(storeID),
                    data.getItemStack(itemPath + "Item"),
                    data.getLong(itemPath + "Price"),
                    data.getLong(itemPath + "Expire-Time")));
                }
            }
        }
        if (data.contains(path + "Bidding")) {
            for (String storeID : data.getConfigurationSection(path + "Bidding").getKeys(false)) {
                String itemPath = path + "Bidding." + storeID + ".";
                Calendar expire = Calendar.getInstance();
                expire.setTimeInMillis(data.getLong(itemPath + "Expire-Time"));
                if (now.after(expire)) {
                    items.addExpiredItem(new ExpiredItem(uuid, name, UUID.fromString(storeID), data.getItemStack(itemPath + "Item")));
                } else {
                    items.addBiddingItem(new BidItem(uuid, name, UUID.fromString(storeID),
                    new TopBidder(UUID.fromString(data.getString(itemPath + "Top-Bidder.UUID")), data.getString(itemPath + "Top-Bidder.Name"), data.getInt(itemPath + "Top-Bidder.Bid")),
                    data.getItemStack(itemPath + "Item"),
                    data.getLong(itemPath + "Price"),
                    data.getLong(itemPath + "Expire-Time")));
                }
            }
        }
        if (data.contains(path + "Expired")) {
            for (String storeID : data.getConfigurationSection(path + "Expired").getKeys(false)) {
                String itemPath = path + "Expired." + storeID + ".";
                items.addExpiredItem(new ExpiredItem(uuid, name, UUID.fromString(storeID),
                data.getItemStack(itemPath + "Item"),
                data.getLong(itemPath + "Expire-Time")));
            }
        }
        return items;
    }
    
}