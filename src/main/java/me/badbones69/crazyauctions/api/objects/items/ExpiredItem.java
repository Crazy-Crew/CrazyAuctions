package me.badbones69.crazyauctions.api.objects.items;

import me.badbones69.crazyauctions.api.interfaces.AuctionItem;
import me.badbones69.crazyauctions.api.managers.TimeManager;
import me.badbones69.crazyauctions.api.multiworld.PerWorld;
import me.badbones69.crazyauctions.api.multiworld.WorldGroup;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.UUID;

public class ExpiredItem implements AuctionItem {
    
    private UUID ownerUUID;
    private String ownerName;
    private UUID storeID;
    private ItemStack item;
    private long expireTime;
    private Calendar expire;
    
    public ExpiredItem(SellItem sellItem) {
        new ExpiredItem(sellItem.getOwnerUUID(), sellItem.getOwnerName(), sellItem.getStoreID(), sellItem.getItem());
    }
    
    public ExpiredItem(BidItem bidItem) {
        new ExpiredItem(bidItem.getOwnerUUID(), bidItem.getOwnerName(), bidItem.getStoreID(), bidItem.getItem());
    }
    
    public ExpiredItem(UUID ownerUUID, String ownerName, UUID storeID, ItemStack item) {
        new ExpiredItem(ownerUUID, ownerName, storeID, item, TimeManager.EXPIRE_TIME.getTime().getTimeInMillis());
    }
    
    public ExpiredItem(UUID ownerUUID, String ownerName, UUID storeID, ItemStack item, long expireTime) {
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.storeID = storeID;
        this.item = item;
        this.expireTime = expireTime;
        expire = Calendar.getInstance();
        expire.setTimeInMillis(expireTime);
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
    
    public ItemStack getItem() {
        return item;
    }
    
    @Override
    public long getPrice() {
        return 0;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
    public boolean isExpired() {
        return Calendar.getInstance().after(expire);
    }
    
    @Override
    public PerWorld getPerWorld() {
        return null;
    }
    
    @Override
    public WorldGroup getWorldGroup() {
        return null;
    }
    
    @Override
    public boolean isPerWorld() {
        return false;
    }
    
    @Override
    public boolean isMultiWorld() {
        return false;
    }
    
    @Override
    public void setSold(boolean sold) {
        //Empty
    }
    
    @Override
    public boolean isSold() {
        return false;
    }
    
}