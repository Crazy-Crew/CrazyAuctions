package me.badbones69.crazyauctions.api.managers;

import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.enums.ShopType;
import me.badbones69.crazyauctions.api.interfaces.AuctionItem;
import me.badbones69.crazyauctions.api.multiworld.PerWorld;
import me.badbones69.crazyauctions.api.multiworld.SingleAuctionHouse;
import me.badbones69.crazyauctions.api.multiworld.WorldGroup;
import me.badbones69.crazyauctions.api.objects.gui.AuctionHouse;
import me.badbones69.crazyauctions.api.objects.items.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AuctionManager {
    
    private static AuctionManager instance = new AuctionManager();
    private MultiWorldManager multiWorldManager = MultiWorldManager.getInstance();
    private SingleAuctionHouse singleAuctionHouse = SingleAuctionHouse.getInstance();
    private boolean isSaving = false;
    private ShopType defaultShop;
    private List<UserItems> userItemList = new ArrayList<>();
    
    public void load() {
        userItemList.clear();
        userItemList.addAll(loadUserItems());
        defaultShop = ShopType.SELL;
        startUpAuctionHouses();
    }
    
    public static AuctionManager getInstance() {
        return instance;
    }
    
    public boolean isAuctionHouseSaving() {
        return isSaving;
    }
    
    public AuctionHouse createAuctionHouse(ShopType shopType, List<SellItem> sellingItems, List<BidItem> biddingItems) {
        return new AuctionHouse(biddingItems.size() + 1, shopType, sellingItems, biddingItems);
    }
    
    public void openAuctionHouse(Player player) {
        openAuctionHouse(player, defaultShop, 1);
    }
    
    public void openAuctionHouse(Player player, ShopType shopType) {
        openAuctionHouse(player, shopType, 1);
    }
    
    public void openAuctionHouse(Player player, ShopType shopType, int page) {
        getAuctionHouse(player, shopType, page).openInventory(player);
    }
    
    public AuctionHouse getAuctionHouse(Player player) {
        return getAuctionHouse(player, defaultShop, 1);
    }
    
    public AuctionHouse getAuctionHouse(Player player, ShopType shopType) {
        return getAuctionHouse(player, shopType, 1);
    }
    
    public AuctionHouse getAuctionHouse(Player player, ShopType shopType, int page) {
        if (multiWorldManager.isEnabled()) {
            if (multiWorldManager.isPerWorld()) {
                return multiWorldManager.getPerWorld(player).getAuctionHouse(shopType, page);
            } else {
                return multiWorldManager.getWorldGroup(player).getAuctionHouse(shopType, page);
            }
        } else {
            return singleAuctionHouse.getAuctionHouse(shopType, page);
        }
    }
    
    public AuctionHouse getAuctionHouse(AuctionItem auctionItem) {
        if (multiWorldManager.isEnabled()) {
            if (multiWorldManager.isPerWorld()) {
                return multiWorldManager.getPerWorld(auctionItem).getAuctionHouse(shopType, page);
            } else {
                return multiWorldManager.getWorldGroup(auctionItem).getAuctionHouse(shopType, page);
            }
        } else {
            return singleAuctionHouse.getAuctionHouse(shopType, page);
        }
    }
    
    public int getMaxPage(int size) {
        int maxPage = 1;
        for (; size > 45; size -= 45, maxPage++) ;
        return maxPage;
    }
    
    public void addAuctionItem(AuctionItem auctionItem) {
        addAuctionItem(auctionItem, getUserItems(auctionItem));
    }
    
    public void addAuctionItem(AuctionItem auctionItem, UserItems userItems) {
        userItems.addAuctionItem(auctionItem);
        if (multiWorldManager.isEnabled()) {
            if (multiWorldManager.isPerWorld() && auctionItem.isPerWorld()) {
                auctionItem.getPerWorld().addAuctionItem(auctionItem);
            } else if (auctionItem.isMultiWorld()) {
                auctionItem.getWorldGroup().addAuctionItem(auctionItem);
            }
        } else {
            singleAuctionHouse.addAuctionItem(auctionItem);
        }
    }
    
    public void removeAuctionItem(AuctionItem auctionItem) {
        removeAuctionItem(auctionItem, getUserItems(auctionItem));
    }
    
    public void removeAuctionItem(AuctionItem auctionItem, UserItems userItems) {
        userItems.removeAuctionItem(auctionItem);
        if (multiWorldManager.isEnabled()) {
            if (multiWorldManager.isPerWorld() && auctionItem.isPerWorld()) {
                auctionItem.getPerWorld().removeAuctionItem(auctionItem);
            } else if (auctionItem.isMultiWorld()) {
                auctionItem.getWorldGroup().removeAuctionItem(auctionItem);
            }
        } else {
            singleAuctionHouse.removeAuctionItem(auctionItem);
        }
    }
    
    public UserItems getUserItems(AuctionItem sellItem) {
        return getUserItems(sellItem.getOwnerUUID());
    }
    
    public UserItems getUserItems(Player player) {
        return getUserItems(player.getUniqueId());
    }
    
    public UserItems getUserItems(UUID uuid) {
        for (UserItems item : userItemList) {
            if (item.getOwnerUUID().equals(uuid)) {
                return item;
            }
        }
        UserItems userItems = new UserItems(uuid, Bukkit.getOfflinePlayer(uuid).getName());
        userItemList.add(userItems);
        return userItems;
    }
    
    private void startUpAuctionHouses() {
        if (multiWorldManager.isEnabled()) {
            if (multiWorldManager.isPerWorld()) {
                for (PerWorld perWorld : multiWorldManager.getPerWorlds()) {
                    perWorld.startUpAuctionHouses();
                }
            } else {
                for (WorldGroup worldGroup : multiWorldManager.getWorldGroups()) {
                    worldGroup.startUpAuctionHouses();
                }
            }
        } else {
            singleAuctionHouse.startUpAuctionHouses();
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
                        if (sellItem.isMultiWorld()) {
                            if (sellItem.isPerWorld()) {
                                data.set(itemPath + "World", sellItem.getPerWorld().getWorldName());
                            } else {
                                data.set(itemPath + "World-Group", sellItem.getWorldGroup().getID());
                            }
                        }
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
                        if (bidItem.isMultiWorld()) {
                            if (bidItem.isPerWorld()) {
                                data.set(itemPath + "World", bidItem.getPerWorld());
                            } else {
                                data.set(itemPath + "World-Group", bidItem.getWorldGroup().getID());
                            }
                        }
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
    
    private List<UserItems> loadUserItems() {
        FileConfiguration data = Files.DATA.getFile();
        List<UserItems> userItems = new ArrayList<>();
        if (data.contains("Users")) {
            for (String uuidString : data.getConfigurationSection("Users").getKeys(false)) {
                String path = "Users." + uuidString + ".";
                UUID uuid = UUID.fromString(uuidString);
                String name = data.getString(path + "Name");
                Calendar now = Calendar.getInstance();
                UserItems userItem = new UserItems(uuid, name);
                if (data.contains(path + "Selling")) {
                    for (String storeID : data.getConfigurationSection(path + "Selling").getKeys(false)) {
                        String itemPath = path + "Selling." + storeID + ".";
                        Calendar expire = Calendar.getInstance();
                        expire.setTimeInMillis(data.getLong(itemPath + "Expire-Time"));
                        if (now.after(expire)) {
                            addAuctionItem(new ExpiredItem(uuid, name, UUID.fromString(storeID), data.getItemStack(itemPath + "Item")), userItem);
                        } else {
                            addAuctionItem(new SellItem(uuid, name, UUID.fromString(storeID),
                            data.getItemStack(itemPath + "Item"),
                            data.getLong(itemPath + "Price"),
                            data.getLong(itemPath + "Expire-Time"),
                            multiWorldManager.getPerWorld(data.getString(path + "World", "")),
                            multiWorldManager.getWorldGroup(data.getString(path + "World-Group"))), userItem);
                        }
                    }
                }
                if (data.contains(path + "Bidding")) {
                    for (String storeID : data.getConfigurationSection(path + "Bidding").getKeys(false)) {
                        String itemPath = path + "Bidding." + storeID + ".";
                        Calendar expire = Calendar.getInstance();
                        expire.setTimeInMillis(data.getLong(itemPath + "Expire-Time"));
                        if (now.after(expire)) {
                            addAuctionItem(new ExpiredItem(uuid, name, UUID.fromString(storeID), data.getItemStack(itemPath + "Item")), userItem);
                        } else {
                            addAuctionItem(new BidItem(uuid, name, UUID.fromString(storeID),
                            new TopBidder(UUID.fromString(data.getString(itemPath + "Top-Bidder.UUID")), data.getString(itemPath + "Top-Bidder.Name"), data.getInt(itemPath + "Top-Bidder.Bid")),
                            data.getItemStack(itemPath + "Item"),
                            data.getLong(itemPath + "Price"),
                            data.getLong(itemPath + "Expire-Time"),
                            multiWorldManager.getPerWorld(data.getString(path + "World", "")),
                            multiWorldManager.getWorldGroup(data.getString(path + "World-Group"))), userItem);
                        }
                    }
                }
                if (data.contains(path + "Expired")) {
                    for (String storeID : data.getConfigurationSection(path + "Expired").getKeys(false)) {
                        String itemPath = path + "Expired." + storeID + ".";
                        addAuctionItem(new ExpiredItem(uuid, name, UUID.fromString(storeID),
                        data.getItemStack(itemPath + "Item"),
                        data.getLong(itemPath + "Expire-Time")), userItem);
                    }
                }
                userItems.add(userItem);
            }
        }
        return userItems;
    }
    
}