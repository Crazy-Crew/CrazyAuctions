package me.badbones69.crazyauctions.api.objects.items;

import me.badbones69.crazyauctions.api.managers.TimeManager;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ExpiredItem {
    
    private UUID ownerUUID;
    private String ownerName;
    private UUID storeID;
    private ItemStack item;
    private long expireTime;
    
    public ExpiredItem(UUID ownerUUID, String ownerName, UUID storeID, ItemStack item) {
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.storeID = storeID;
        this.item = item;
        expireTime = TimeManager.EXPIRE_TIME.getTime().getTimeInMillis();
    }
    
    public ExpiredItem(UUID ownerUUID, String ownerName, UUID storeID, ItemStack item, long expireTime) {
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.storeID = storeID;
        this.item = item;
        this.expireTime = expireTime;
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
    
    public long getExpireTime() {
        return expireTime;
    }
    
}