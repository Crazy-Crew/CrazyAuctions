package me.badbones69.crazyauctions.api.events;

import me.badbones69.crazyauctions.api.objects.items.TopBidder;
import me.badbones69.crazyauctions.api.objects.items.BidItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BadBones69
 *
 * This event is fired when a bidding item's time has run out and so a player wins the item.
 *
 */
public class AuctionWinBidEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private BidItem bidItem;
    private TopBidder topBidder;
    
    public AuctionWinBidEvent(BidItem bidItem) {
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