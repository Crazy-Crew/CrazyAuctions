package com.badbones69.crazyauctions.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * @author BadBones69
 *
 * This event is fired when a bidding item's time has run out and so a player wins the item.
 */
public class AuctionWinBidEvent extends Event {
    
    private final HandlerList handlers = new HandlerList();
    private final Player player;
    private final long bid;
    private final ItemStack item;
    
    /**
     * @param player The player who won the item.
     * @param item The item that was won.
     * @param bid The bid that was placed on the item.
     */
    public AuctionWinBidEvent(Player player, ItemStack item, long bid) {
        this.player = player;
        this.item = item;
        this.bid = bid;
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