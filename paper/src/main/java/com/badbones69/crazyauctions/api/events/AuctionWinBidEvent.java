package com.badbones69.crazyauctions.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author BadBones69
 *
 * This event is fired when a bidding item's time has run out and so a player wins the item.
 *
 */
public class AuctionWinBidEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final double bid;
    private final ItemStack item;
    private final String currency;
    
    /**
     *
     * @param player The player who won the item.
     * @param item The item that was won.
     * @param bid The bid that was placed on the item.
     */
    public AuctionWinBidEvent(Player player, ItemStack item, double bid, String currency) {
        this.player = player;
        this.item = item;
        this.bid = bid;
        this.currency = currency;
    }

    public AuctionWinBidEvent(Player player, ItemStack item, double bid) {
        this(player, item, bid, "");
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public ItemStack getItem() {
        return this.item;
    }
    
    public double getBid() {
        return this.bid;
    }

    public String getCurrency() {
        return this.currency;
    }
}