package us.crazycrew.crazyauctions.api.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Created: 2/18/2023
 * Time: Unknown
 * Last Edited: 2/28/2023 @ 3:04 AM
 *
 * Description: This event is fired when a player buys an item.
 */
public class AuctionBuyEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final UUID uuid;
    private final Player player;

    private final ItemStack item;

    private final long price;

    /**
     * A constructor to include values for a buy event.
     *
     * @param uuid the uuid of the player who purchased the item.
     * @param item the item that was purchased.
     * @param price the amount of money used to buy.
     */
    public AuctionBuyEvent(UUID uuid, ItemStack item, long price) {
        this.uuid = uuid;

        this.player = Bukkit.getPlayer(uuid);

        this.item = item;

        this.price = price;
    }

    public ItemStack getWinningItem() {
        return this.item;
    }

    public long getBuyPrice() {
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