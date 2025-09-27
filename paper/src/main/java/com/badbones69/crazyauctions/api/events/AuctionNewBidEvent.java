package com.badbones69.crazyauctions.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 *
 * This event is fired when a player places a new bid onto an item in the auction house.
 */
public class AuctionNewBidEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final double bid;
    private final ItemStack item;
    private final String currency;
    
    /**
     *
     * @param player The player who placed the bid.
     * @param item The item that was bid on.
     * @param bid The amount of money that was bid.
     */
    public AuctionNewBidEvent(Player player, ItemStack item, double bid, String currency) {
        this.player = player;
        this.item = item;
        this.bid = bid;
        this.currency = currency;
    }

    public AuctionNewBidEvent(Player player, ItemStack item, double bid) {
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