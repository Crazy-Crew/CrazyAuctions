package com.badbones69.crazyauctions.api.events;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

/**
 * Description: This event is fired when an auction is cancelled.
 */
public class AuctionCancelEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final UUID uuid;
    private final Player player;
    private final OfflinePlayer offlinePlayer;

    private final ItemStack item;

    /**
     * A constructor to include values for when an item is cancelled.
     *
     * @param uuid the uuid of the player whose item cancelled.
     */
    public AuctionCancelEvent(UUID uuid, ItemStack item) {
        this.uuid = uuid;

        this.player = Bukkit.getPlayer(uuid);

        this.offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        this.item = item;
    }

    /**
     * @return the expired item.
     */
    public ItemStack getExpiredItem() {
        return this.item;
    }

    /**
     * Only use this when the player is online.
     *
     * @return the online player.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Only use this when the player is offline.
     *
     * @return the offline player.
     */
    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    /**
     * @return the player's uuid.
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * @return the handler list.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}