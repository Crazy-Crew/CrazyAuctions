package com.badbones69.crazyauctions.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 *
 * This event is fired when a player places a new bid onto an item in the auction house.
 */
public class AuctionNewBidEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final long bid;
    private final ItemStack item;
    
    /**
     *
     * @param player The player who placed the bid.
     * @param item The item that was bid on.
     * @param bid The amount of money that was bid.
     */
    public AuctionNewBidEvent(Player player, ItemStack item, long bid) {
        this.player = player;
        this.item = item;
        this.bid = bid;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public long getBid() {
        return bid;
    }
    
}