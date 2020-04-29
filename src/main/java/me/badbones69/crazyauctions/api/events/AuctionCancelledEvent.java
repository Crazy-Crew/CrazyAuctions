package me.badbones69.crazyauctions.api.events;

import me.badbones69.crazyauctions.api.enums.CancelledReason;
import me.badbones69.crazyauctions.api.objects.items.BidItem;
import me.badbones69.crazyauctions.api.objects.items.SellItem;
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
    private SellItem sellItem;
    private BidItem bidItem;
    private CancelledReason reason;
    private boolean isSell;
    
    public AuctionCancelledEvent(SellItem sellItem, CancelledReason reason) {
        this.sellItem = sellItem;
        this.reason = reason;
        isSell = true;
    }
    
    public AuctionCancelledEvent(BidItem bidItem, CancelledReason reason) {
        this.bidItem = bidItem;
        this.reason = reason;
        isSell = false;
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
    
    public BidItem getBidItem() {
        return bidItem;
    }
    
    public boolean isSellItem() {
        return isSell;
    }
    
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(isSell ? sellItem.getOwnerUUID() : bidItem.getOwnerUUID());
    }
    
    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(isSell ? sellItem.getOwnerUUID() : bidItem.getOwnerUUID());
    }
    
    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }
    
    public ItemStack getItem() {
        return isSell ? sellItem.getItem() : bidItem.getItem();
    }
    
    public CancelledReason getReason() {
        return reason;
    }
    
}