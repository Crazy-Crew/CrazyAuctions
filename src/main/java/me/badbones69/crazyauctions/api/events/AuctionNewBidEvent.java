package me.badbones69.crazyauctions.api.events;

import me.badbones69.crazyauctions.api.objects.TopBidder;
import me.badbones69.crazyauctions.api.objects.items.BidItem;
import org.bukkit.Bukkit;
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
    private BidItem bidItem;
    private TopBidder topBidder;
    
    public AuctionNewBidEvent(BidItem bidItem) {
        this.bidItem = bidItem;
        topBidder = bidItem.getTopBidder();
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public BidItem getBidItem() {
        return bidItem;
    }
    
    public TopBidder getTopBidder() {
        return topBidder;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(topBidder.getOwnerUUID());
    }
    
    public ItemStack getItem() {
        return bidItem.getItem();
    }
    
    public long getBid() {
        return topBidder.getBid();
    }
    
}