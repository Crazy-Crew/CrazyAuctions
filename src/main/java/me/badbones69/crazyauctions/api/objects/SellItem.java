package me.badbones69.crazyauctions.api.objects;

import me.badbones69.crazyauctions.api.managers.TimeManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SellItem {
    
    private UUID ownerUUID;
    private String ownerName;
    private UUID storeID;
    private ItemStack item;
    private long price;
    private long expireTime;
    
    public SellItem(Player owner, ItemStack item, long price) {
        ownerUUID = owner.getUniqueId();
        ownerName = owner.getName();
        storeID = UUID.randomUUID();
        this.item = item;
        this.price = price;
        expireTime = TimeManager.SELL_TIME.getTime().getTimeInMillis();
    }
    
    public SellItem(UUID ownerUUID, String ownerName, UUID storeID, ItemStack item, long price, long expireTime) {
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.storeID = storeID;
        this.item = item;
        this.price = price;
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
    
    public long getPrice() {
        return price;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
}