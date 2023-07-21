package com.badbones69.crazyauctions.api.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Description: This event is fired when an auction has a winner.
 */
public class AuctionWinEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final UUID uuid;
    private final Player player;

    private final ItemStack item;

    private final long price;

    /**
     * A constructor to include values for a win event.
     *
     * @param uuid the uuid of the player who won the bid.
     * @param item the item that was won.
     * @param price the amount of money that was bid.
     */
    public AuctionWinEvent(UUID uuid, ItemStack item, long price) {
        this.uuid = uuid;

        this.player = Bukkit.getPlayer(uuid);

        this.item = item;

        this.price = price;
    }

    public ItemStack getWinningItem() {
        return this.item;
    }

    public long getWinningPrice() {
        return this.price;
    }

    public Player getPlayer() {
        return this.player;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}