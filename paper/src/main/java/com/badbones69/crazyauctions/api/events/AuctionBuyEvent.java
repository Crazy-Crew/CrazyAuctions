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
 * This event is fired when a player buys something from the selling auction house.
 *
 */
public class AuctionBuyEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final double price;
    private final ItemStack item;
    private final String currency;
    
    /**
     *
     * @param player The player who bought the item.
     * @param item The item that was bought.
     * @param price The price of the item.
     */
    public AuctionBuyEvent(Player player, ItemStack item, double price, String currency) {
        this.player = player;
        this.item = item;
        this.price = price;
        this.currency = currency;
    }

    public AuctionBuyEvent(Player player, ItemStack item, double price) {
        this(player, item, price, "");
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
    
    public double getPrice() {
        return this.price;
    }

    public String getCurrency() {
        return this.currency;
    }
}