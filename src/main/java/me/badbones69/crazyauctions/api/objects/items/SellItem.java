package me.badbones69.crazyauctions.api.objects.items;

import me.badbones69.crazyauctions.api.interfaces.AuctionItem;
import me.badbones69.crazyauctions.api.managers.TimeManager;
import me.badbones69.crazyauctions.api.multiworld.MultiWorldManager;
import me.badbones69.crazyauctions.api.multiworld.PerWorld;
import me.badbones69.crazyauctions.api.multiworld.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.UUID;

public class SellItem implements AuctionItem {
    
    private UUID ownerUUID;
    private String ownerName;
    private UUID storeID;
    private ItemStack item;
    private long price;
    private long expireTime;
    private Calendar expire;
    private boolean sold;
    private PerWorld perWorld;
    private WorldGroup worldGroup;
    private MultiWorldManager multiWorldManager = MultiWorldManager.getInstance();
    
    public SellItem(Player owner, ItemStack item, long price) {
        new SellItem(owner, item, price, null, null);
    }
    
    public SellItem(Player owner, ItemStack item, long price, PerWorld perWorld, WorldGroup worldGroup) {
        new SellItem(owner.getUniqueId(), owner.getName(), UUID.randomUUID(), item, price, TimeManager.SELL_TIME.getTime().getTimeInMillis(), perWorld, worldGroup);
    }
    
    public SellItem(UUID ownerUUID, String ownerName, UUID storeID, ItemStack item, long price, long expireTime) {
        new SellItem(ownerUUID, ownerName, storeID, item, price, expireTime, null, null);
    }
    
    public SellItem(UUID ownerUUID, String ownerName, UUID storeID, ItemStack item, long price, long expireTime, PerWorld perWorld, WorldGroup worldGroup) {
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.storeID = storeID;
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
    
    public boolean isSold() {
        return sold;
    }
    
}