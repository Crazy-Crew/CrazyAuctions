package me.badbones69.crazyauctions.api.events;

import me.badbones69.crazyauctions.api.ShopType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BadBones69
 *
 * This event is fired when a new item is listed onto the auction house.
 *
 */
public class AuctionListEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private long price;
    private ShopType shop;
    private ItemStack item;
    
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
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public ShopType getShopType() {
        return shop;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public long getPrice() {
        return price;
    }
    
}