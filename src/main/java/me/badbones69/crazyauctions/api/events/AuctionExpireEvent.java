package me.badbones69.crazyauctions.api.events;

import me.badbones69.crazyauctions.api.objects.items.ExpiredItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BadBones69
 *
 * This event is fired when a player item expires.
 *
 */
public class AuctionExpireEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private ExpiredItem expiredItem;
    
    public AuctionExpireEvent(ExpiredItem expiredItem) {
        this.expiredItem = expiredItem;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public ExpiredItem getExpiredItem() {
        return expiredItem;
    }
    
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(expiredItem.getOwnerUUID());
    }
    
    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(expiredItem.getOwnerUUID());
    }
    
    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }
    
    public ItemStack getItem() {
        return expiredItem.getItem();
    }
    
}