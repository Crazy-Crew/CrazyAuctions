package com.badbones69.crazyauctions.api.events;

import com.badbones69.crazyauctions.api.enums.Reaons;
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

    private final boolean isOnline;
    private final ItemStack item;
    private final Reaons reason;

    private OfflinePlayer offlinePlayer;
    private Player onlinePlayer;
    
    /**
     *
     * @param offlinePlayer The player whose item is cancelled.
     * @param item The item that is cancelled.
     */
    public AuctionCancelledEvent(OfflinePlayer offlinePlayer, ItemStack item, Reaons reason) {
        this.offlinePlayer = offlinePlayer;
        this.item = item;
        this.isOnline = false;
        this.reason = reason;
    }
    
    /**
     *
     * @param onlinePlayer The player whose item is cancelled.
     * @param item The item that is cancelled.
     */
    public AuctionCancelledEvent(Player onlinePlayer, ItemStack item, Reaons reason) {
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
    
    public Reaons getReason() {
        return reason;
    }
    
}