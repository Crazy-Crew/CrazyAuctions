package com.badbones69.crazyauctions.api.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author BadBones69
 *
 * This event is fired when a player item expires.
 *
 */
public class AuctionExpireEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();

    private final boolean isOnline;
    private final ItemStack item;

    private OfflinePlayer offlinePlayer;
    private Player onlinePlayer;
    
    /**
     *
     * @param offlinePlayer The player whose item is expiring.
     * @param item The item that is expiring.
     */
    public AuctionExpireEvent(OfflinePlayer offlinePlayer, ItemStack item) {
        this.offlinePlayer = offlinePlayer;
        this.item = item;
        this.isOnline = false;
    }
    
    /**
     *
     * @param onlinePlayer The player whose item is expiring.
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
    
    public @NotNull HandlerList getHandlers() {
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