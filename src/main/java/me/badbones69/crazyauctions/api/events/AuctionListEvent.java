package me.badbones69.crazyauctions.api.events;

import me.badbones69.crazyauctions.api.enums.ShopType;
import me.badbones69.crazyauctions.api.interfaces.AuctionItem;
import org.bukkit.Bukkit;
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
    private AuctionItem auctionItem;
    private ShopType shop;
    
    public AuctionListEvent(AuctionItem auctionItem) {
        this.auctionItem = auctionItem;
        this.shop = ShopType.SELL;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public AuctionItem getAuctionItem() {
        return auctionItem;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(auctionItem.getOwnerUUID());
    }
    
    public ShopType getShopType() {
        return shop;
    }
    
    public ItemStack getItem() {
        return auctionItem.getItem();
    }
    
    public long getPrice() {
        return auctionItem.getPrice();
    }
    
}