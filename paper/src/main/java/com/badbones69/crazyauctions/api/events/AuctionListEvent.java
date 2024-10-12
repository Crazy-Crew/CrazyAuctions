package com.badbones69.crazyauctions.api.events;

import com.badbones69.crazyauctions.api.enums.ShopType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author BadBones69
 *
 * This event is fired when a new item is listed onto the auction house.
 */
public class AuctionListEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final long price;
    private final ShopType shop;
    private final ItemStack item;
    
    /**
     *
     * @param player The player who is listing the item.
     * @param shop The shop type the item is being listed to.
     * @param item The item being listed.
     * @param price The price the item is being listed for.
     */
    public AuctionListEvent(Player player, ShopType shop, ItemStack item, long price) {
        this.player = player;
        this.shop = shop;
        this.item = item;
        this.price = price;
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
    
    public ShopType getShopType() {
        return this.shop;
    }
    
    public ItemStack getItem() {
        return this.item;
    }
    
    public long getPrice() {
        return this.price;
    }
}