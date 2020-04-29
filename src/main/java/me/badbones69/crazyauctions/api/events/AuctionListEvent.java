package me.badbones69.crazyauctions.api.events;

import me.badbones69.crazyauctions.api.enums.ShopType;
import me.badbones69.crazyauctions.api.objects.items.BidItem;
import me.badbones69.crazyauctions.api.objects.items.SellItem;
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
    private SellItem sellItem;
    private BidItem bidItem;
    private ShopType shop;
    private boolean isSell;
    
    public AuctionListEvent(SellItem sellItem) {
        this.sellItem = sellItem;
        this.shop = ShopType.SELL;
        isSell = true;
    }
    
    public AuctionListEvent(BidItem bidItem) {
        this.bidItem = bidItem;
        this.shop = ShopType.BID;
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
    
    public Player getPlayer() {
        return Bukkit.getPlayer(isSell ? sellItem.getOwnerUUID() : bidItem.getOwnerUUID());
    }
    
    public ShopType getShopType() {
        return shop;
    }
    
    public ItemStack getItem() {
        return isSell ? sellItem.getItem() : bidItem.getItem();
    }
    
    public long getPrice() {
        return isSell ? sellItem.getPrice() : bidItem.getPrice();
    }
    
}