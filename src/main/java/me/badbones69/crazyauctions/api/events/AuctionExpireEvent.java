package me.badbones69.crazyauctions.api.events;

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
    private OfflinePlayer offlinePlayer;
    private Player onlinePlayer;
    private boolean isOnline;
    private ItemStack item;
    
    /**
     *
     * @param offlinePlayer The player who's item is expiring.
     * @param item The item that is expiring.
     */
    public AuctionExpireEvent(OfflinePlayer offlinePlayer, ItemStack item) {
        this.offlinePlayer = offlinePlayer;
        this.item = item;
        this.isOnline = false;
    }
    
    /**
     *
     * @param onlinePlayer The player who's item is expiring.
     * @param item The item that is expiring.
     */
    public AuctionExpireEvent(Player onlinePlayer, ItemStack item) {
        this.onlinePlayer = onlinePlayer;
        this.item = item;
        this.isOnline = true;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }
    
    public Player getOnlinePlayer() {
        return onlinePlayer;
    }
    
    public boolean isOnline() {
        return isOnline;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
}