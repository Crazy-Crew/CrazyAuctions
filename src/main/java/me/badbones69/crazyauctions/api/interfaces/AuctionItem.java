package me.badbones69.crazyauctions.api.interfaces;

import me.badbones69.crazyauctions.api.multiworld.PerWorld;
import me.badbones69.crazyauctions.api.multiworld.WorldGroup;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface AuctionItem {
    
    UUID getOwnerUUID();
    
    String getOwnerName();
    
    UUID getStoreID();
    
    ItemStack getItem();
    
    long getPrice();
    
    long getExpireTime();
    
    boolean isExpired();
    
    PerWorld getPerWorld();
    
    WorldGroup getWorldGroup();
    
    boolean isPerWorld();
    
    boolean isMultiWorld();
    
    void setSold(boolean sold);
    
    boolean isSold();
    
}