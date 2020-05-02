package me.badbones69.crazyauctions.api.objects.items;

import me.badbones69.crazyauctions.api.interfaces.AuctionItem;
import me.badbones69.crazyauctions.api.managers.TimeManager;
import me.badbones69.crazyauctions.api.multiworld.PerWorld;
import me.badbones69.crazyauctions.api.multiworld.WorldGroup;
import me.badbones69.crazyauctions.api.objects.TopBidder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.UUID;

public class BidItem implements AuctionItem {
    
    private UUID ownerUUID;
    private String ownerName;
    private UUID storeID;
    private ItemStack item;
    private TopBidder topBidder;
    private long price;
    private long expireTime;
    private Calendar expire;
    private PerWorld perWorld;
    private WorldGroup worldGroup;
    private boolean sold;
    
    public BidItem(Player owner, ItemStack item, long price) {
        new BidItem(owner, item, price, null, null);
    }
    
    public BidItem(Player owner, ItemStack item, long price, PerWorld perWorld, WorldGroup worldGroup) {
        new BidItem(owner.getUniqueId(), owner.getName(), UUID.randomUUID(), new TopBidder(), item, price, TimeManager.BID_TIME.getTime().getTimeInMillis(), perWorld, worldGroup);
    }
    
    public BidItem(UUID ownerUUID, String ownerName, UUID storeID, TopBidder topBidder, ItemStack item, long price, long expireTime) {
        new BidItem(ownerUUID, ownerName, storeID, topBidder, item, price, expireTime, null, null);
    }
    
    public BidItem(UUID ownerUUID, String ownerName, UUID storeID, TopBidder topBidder, ItemStack item, long price, long expireTime, PerWorld perWorld, WorldGroup worldGroup) {
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.storeID = storeID;
        this.topBidder = topBidder;
        this.item = item;
        this.price = price;
        this.expireTime = expireTime;
        expire = Calendar.getInstance();
        expire.setTimeInMillis(expireTime);
        this.perWorld = perWorld;
        this.worldGroup = worldGroup;
        sold = false;
    }
    
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public UUID getStoreID() {
        return storeID;
    }
    
    public TopBidder getTopBidder() {
        return topBidder;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public long getPrice() {
        return price;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
    public boolean isExpired() {
        return Calendar.getInstance().after(expire);
    }
    
    public PerWorld getPerWorld() {
        return perWorld;
    }
    
    public WorldGroup getWorldGroup() {
        return worldGroup;
    }
    
    public boolean isPerWorld() {
        return perWorld != null;
    }
    
    public boolean isMultiWorld() {
        return perWorld == null && worldGroup != null;
    }
    
    public void setSold(boolean sold) {
        this.sold = sold;
    }
    
    public boolean isSold() {
        return sold;
    }
    
}