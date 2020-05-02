package me.badbones69.crazyauctions.api.events;

import me.badbones69.crazyauctions.api.enums.CancelledReason;
import me.badbones69.crazyauctions.api.interfaces.AuctionItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BadBones69
 *
 * This event is fired when a player's item is cancelled.
 *
 */
public class AuctionCancelledEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private AuctionItem auctionItem;
    private CancelledReason reason;
    
    public AuctionCancelledEvent(AuctionItem auctionItem, CancelledReason reason) {
        this.auctionItem = auctionItem;
        this.reason = reason;
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
    
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(auctionItem.getOwnerUUID());
    }
    
    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(auctionItem.getOwnerUUID());
    }
    
    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }
    
    public ItemStack getItem() {
        return auctionItem.getItem();
    }
    
    public CancelledReason getReason() {
        return reason;
    }
    
}