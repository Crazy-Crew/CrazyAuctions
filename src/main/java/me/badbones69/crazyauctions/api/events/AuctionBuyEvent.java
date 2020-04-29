package me.badbones69.crazyauctions.api.events;

import me.badbones69.crazyauctions.api.objects.items.SellItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BadBones69
 *
 * This event is fired when a player buys something from the selling auction house.
 *
 */
public class AuctionBuyEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private Player buyer;
    private SellItem sellItem;
    
    public AuctionBuyEvent(Player buyer, SellItem sellItem) {
        this.buyer = buyer;
        this.sellItem = sellItem;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public SellItem getSellItem() {
        return sellItem;
    }
    
    public Player getPlayer() {
        return buyer;
    }
    
    public ItemStack getItem() {
        return sellItem.getItem();
    }
    
    public long getPrice() {
        return sellItem.getPrice();
    }
    
}