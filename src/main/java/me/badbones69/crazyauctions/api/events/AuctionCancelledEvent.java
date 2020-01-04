package me.badbones69.crazyauctions.api.events;

import me.badbones69.crazyauctions.api.enums.CancelledReason;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BadBones69
 *
 * This event is fired when a player's item is cancelled.
 *
 */
public class AuctionCancelledEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private OfflinePlayer offlinePlayer;
    private Player onlinePlayer;
    private boolean isOnline;
    private ItemStack item;
    private CancelledReason reason;
    
    /**
     *
     * @param offlinePlayer The player who's item is cancelled.
     * @param item The item that is cancelled.
     */
    public AuctionCancelledEvent(OfflinePlayer offlinePlayer, ItemStack item, CancelledReason reason) {
        this.offlinePlayer = offlinePlayer;
        this.item = item;
        this.isOnline = false;
        this.reason = reason;
    }
    
    /**
     *
     * @param onlinePlayer The player who's item is cancelled.
     * @param item The item that is cancelled.
     */
    public AuctionCancelledEvent(Player onlinePlayer, ItemStack item, CancelledReason reason) {
        this.onlinePlayer = onlinePlayer;
        this.item = item;
        this.isOnline = true;
        this.reason = reason;
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
    
    public CancelledReason getReason() {
        return reason;
    }
    
}