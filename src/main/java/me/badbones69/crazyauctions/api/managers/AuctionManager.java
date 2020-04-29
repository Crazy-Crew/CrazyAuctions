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
    private List<SellItem> sellingItems = new ArrayList<>();
    private List<BidItem> biddingItems = new ArrayList<>();
    private List<ExpiredItem> expiredItems = new ArrayList<>();
    
    public void loadAuctionHouse() {
        sellingItems.clear();
        biddingItems.clear();
        expiredItems.clear();
        //Load Selling, Bidding, and Expired items.
        FileConfiguration data = Files.DATA.getFile();
        if (data.contains("Users")) {
            for (String uuid : data.getConfigurationSection("Users").getKeys(false)) {
                UserItems userItems = getSellingItems(data, "Users." + uuid + ".", uuid);
                sellingItems.addAll(userItems.getSellingItems());
                biddingItems.addAll(userItems.getBiddingItems());
                expiredItems.addAll(userItems.getExpiredItems());
            }
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
    
    public static AuctionManager getInstance() {
        return instance;
    }
    
    private UserItems getSellingItems(FileConfiguration data, String path, String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        String name = data.getString(path + "Name");
        Calendar now = Calendar.getInstance();
        UserItems items = new UserItems();
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