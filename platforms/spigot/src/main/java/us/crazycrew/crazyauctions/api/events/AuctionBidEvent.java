package us.crazycrew.crazyauctions.api.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

/**
 *
 * @author Ryder Belserion
 *
 * This event is fired when a player bids on an auction.
 *
 */
public class AuctionBidEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final UUID uuid;
    private final Player player;

    private final ItemStack item;

    private final long bidPrice;

    /**
     * A constructor to include values for a bid event.
     *
     * @param uuid the uuid of the player who placed the bid.
     * @param item the item that was bid on.
     * @param bidPrice the amount of money that was bid.
     */
    public AuctionBidEvent(UUID uuid, ItemStack item, long bidPrice) {
        this.uuid = uuid;

        this.player = Bukkit.getPlayer(uuid);

        this.item = item;

        this.bidPrice = bidPrice;
    }

    public ItemStack getBidItem() {
        return this.item;
    }

    public long getBidPrice() {
        return this.bidPrice;
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