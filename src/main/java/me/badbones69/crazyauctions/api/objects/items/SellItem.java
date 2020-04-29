package me.badbones69.crazyauctions.api.objects.items;

import me.badbones69.crazyauctions.api.managers.TimeManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.UUID;

public class SellItem {
    
    private UUID ownerUUID;
    private String ownerName;
    private UUID storeID;
    private ItemStack item;
    private long price;
    private long expireTime;
    private Calendar expire;
    private boolean sold;
    
    public SellItem(Player owner, ItemStack item, long price) {
        new SellItem(owner.getUniqueId(), owner.getName(), UUID.randomUUID(), item, price, TimeManager.SELL_TIME.getTime().getTimeInMillis());
    }
    
    public SellItem(UUID ownerUUID, String ownerName, UUID storeID, ItemStack item, long price, long expireTime) {
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.storeID = storeID;
        this.item = item;
        this.price = price;
        this.expireTime = expireTime;
        expire = Calendar.getInstance();
        expire.setTimeInMillis(expireTime);
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
    
    public void setSold(boolean sold) {
        this.sold = sold;
    }
    
    public boolean isSold() {
        return sold;
    }
    
}