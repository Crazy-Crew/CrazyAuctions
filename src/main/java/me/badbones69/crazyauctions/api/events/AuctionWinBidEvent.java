package me.badbones69.crazyauctions.api.events;

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
	private Player player;
	private long bid;
	private ItemStack item;
	
	/**
	 *
	 * @param player
	 * @param item
	 * @param bid
	 */
	public AuctionWinBidEvent(Player player, ItemStack item, long bid) {
		this.player = player;
		this.item = item;
		this.bid = bid;
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
	
	public ItemStack getItem() {
		return item;
	}
	
	public long getBid() {
		return bid;
	}
	
}