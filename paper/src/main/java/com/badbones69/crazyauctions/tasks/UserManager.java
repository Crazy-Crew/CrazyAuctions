package com.badbones69.crazyauctions.tasks;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.Reasons;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.events.AuctionCancelledEvent;
import com.badbones69.crazyauctions.tasks.objects.AuctionItem;
import com.badbones69.crazyauctions.tasks.objects.ExpiredItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();

    private final Map<UUID, List<AuctionItem>> auctions = new HashMap<>();

    private final Map<UUID, List<ExpiredItem>> expired_items = new HashMap<>();

    public final void updateAuctionsCache() {
        this.auctions.clear();

        final FileConfiguration data = Files.data.getConfiguration();

        final ConfigurationSection section = data.getConfigurationSection("active_auctions");

        if (section == null) return;

        for (String key : section.getKeys(false)) {
            final ConfigurationSection player = section.getConfigurationSection(key);

            if (player == null) continue;

            for (String number : player.getKeys(false)) {
                final ConfigurationSection auction = player.getConfigurationSection(number);

                if (auction == null) continue;

                addActiveAuction(key, auction);
            }
        }
    }

    public void addAuctionItem(final Player player, final ItemStack itemStack, final long price, final boolean isBiddable) {
        final FileConfiguration data = Files.data.getConfiguration();
        final FileConfiguration config = Files.config.getConfiguration();

        final String uuid = player.getUniqueId().toString();

        int current_auction = 1;

        if (!data.contains("active_auctions." + uuid)) {
            data.set("active_auctions." + uuid + "." + current_auction + ".name", player.getName());

            final ConfigurationSection section = data.getConfigurationSection("active_auctions." + uuid + "." + current_auction);

            if (section == null) return;

            addItem(itemStack, uuid, price, isBiddable, config, section);

            return;
        }

        for (;data.contains("active_auctions." + uuid + "." + current_auction); current_auction++);

        data.set("active_auctions." + uuid + "." + current_auction + ".name", player.getName());

        final ConfigurationSection section = data.getConfigurationSection("active_auctions." + uuid + "." + current_auction);

        if (section == null) return;

        addItem(itemStack, uuid, price, isBiddable, config, section);
    }

    public void removeAuctionItem(final AuctionItem auction) {
        final FileConfiguration data = Files.data.getConfiguration();

        final ConfigurationSection section = data.getConfigurationSection("active_auctions");

        boolean isNuked = false;

        final UUID uuid = auction.getUuid();

        if (section != null) {
            final ConfigurationSection player_section = section.getConfigurationSection(uuid.toString());

            final String id = auction.getId();

            if (player_section != null && player_section.contains(id)) {
                player_section.set(id, null);

                isNuked = true;
            }
        }

        if (!isNuked || !this.auctions.containsKey(uuid)) return; // only remove from the cache, if the data file removal was successful!

        final List<AuctionItem> auctions = this.auctions.get(uuid);

        auctions.remove(auction);

        this.auctions.put(uuid, auctions);
    }

    public void removeActiveItems(final Player player) {
        final UUID uuid = player.getUniqueId();

        final FileConfiguration data = Files.data.getConfiguration();

        final ConfigurationSection section = data.getConfigurationSection("active_auctions");

        boolean isNuked = false;

        if (section != null) {
            final ConfigurationSection player_section = section.getConfigurationSection(uuid.toString());

            if (player_section != null) {
                section.set(uuid.toString(), null);

                isNuked = true;
            }
        }

        if (!isNuked || !this.auctions.containsKey(uuid)) return; // only remove from cache, if successfully removed from file.

        this.auctions.remove(uuid);
    }

    public final AuctionItem getAuctionItemById(final UUID uuid, final String store_id) {
        AuctionItem key = null;

        for (final AuctionItem auction : this.auctions.get(uuid)) {
            if (!auction.getStoreID().equals(store_id)) continue;

            key = auction;

            break;
        }

        return key;
    }

    public final Map<UUID, List<AuctionItem>> getAuctions() {
        return this.auctions;
    }

    public final boolean hasAuction(final UUID uuid) {
        return this.auctions.containsKey(uuid);
    }

    public final List<AuctionItem> getAuctions(final UUID uuid) {
        return this.auctions.get(uuid);
    }

    public final void updateExpiredCache() {
        this.expired_items.clear();

        final FileConfiguration data = Files.data.getConfiguration();

        final ConfigurationSection section = data.getConfigurationSection("expired_auctions");

        if (section == null) return;

        for (String key : section.getKeys(false)) {
            final ConfigurationSection player = section.getConfigurationSection(key);

            if (player == null) continue;

            for (String number : player.getKeys(false)) {
                final ConfigurationSection auction = player.getConfigurationSection(number);

                if (auction == null) continue;

                addExpiredAuction(key, auction);
            }
        }
    }

    public void addExpiredItem(final Player player, final AuctionItem auction) {
        final AuctionCancelledEvent auctionCancelledEvent = new AuctionCancelledEvent(player, auction.asItemStack(), Reasons.PLAYER_FORCE_CANCEL);
        this.plugin.getServer().getPluginManager().callEvent(auctionCancelledEvent);

        final UUID uuid = player.getUniqueId();

        final FileConfiguration data = Files.data.getConfiguration();

        int number = 1;

        for (;data.contains("expired_auctions." + uuid + "." + number); number++);

        data.set("expired_auctions." + uuid + "." + number + ".name", auction.getName());

        final ConfigurationSection section = data.getConfigurationSection("expired_auctions." + uuid + "." + number);

        if (section == null) return;

        section.set("item", auction.asBase64());
        section.set("store_id", auction.getStoreID());
        section.set("full_time", auction.getFullExpire());
        section.set("price", auction.getPrice());

        // data.set("active_auctions." + uuid + "." + auction.getId(), null);

        if (data.contains("active_auctions." + uuid + "." + auction.getId())) {
            removeAuctionItem(auction);
        }

        Files.data.save();

        final ExpiredItem expiredItem = new ExpiredItem(
                auction.getUuid().toString(),
                auction.getName(),
                String.valueOf(number),
                auction.asBase64(),
                auction.getStoreID(),
                auction.getFullExpire(),
                auction.getPrice()
        );

        if (this.expired_items.containsKey(uuid)) {
            final List<ExpiredItem> items = this.expired_items.get(uuid);

            items.add(expiredItem);

            this.expired_items.put(uuid, items);

            return;
        }

        this.expired_items.put(uuid, new ArrayList<>() {{
            add(expiredItem);
        }});
    }

    public void removeExpiredItem(final ExpiredItem expiredItem) {
        final FileConfiguration data = Files.data.getConfiguration();

        final ConfigurationSection section = data.getConfigurationSection("expired_auctions");

        boolean isNuked = false;

        final UUID uuid = expiredItem.getUuid();

        if (section != null) {
            final ConfigurationSection player_section = section.getConfigurationSection(uuid.toString());

            final String id = expiredItem.getId();

            if (player_section != null && player_section.contains(id)) {
                player_section.set(id, null);

                isNuked = true;
            }
        }

        if (!isNuked || !this.expired_items.containsKey(uuid)) return; // only remove from the cache, if the data file removal was successful!

        final List<ExpiredItem> auctions = this.expired_items.get(uuid);

        auctions.remove(expiredItem);

        this.expired_items.put(expiredItem.getUuid(), auctions);
    }

    public void removeExpiredItems(final Player player) {
        final UUID uuid = player.getUniqueId();

        final FileConfiguration data = Files.data.getConfiguration();

        final ConfigurationSection section = data.getConfigurationSection("expired_auctions");

        boolean isNuked = false;

        if (section != null) {
            final ConfigurationSection player_section = section.getConfigurationSection(uuid.toString());

            if (player_section != null) {
                section.set(uuid.toString(), null);

                isNuked = true;
            }
        }

        if (!isNuked || !this.expired_items.containsKey(uuid)) return; // only remove from cache, if successfully removed from file.

        this.expired_items.remove(uuid);
    }

    public final ExpiredItem getExpiredItemById(final UUID uuid, final String store_id) {
        ExpiredItem key = null;

        for (final ExpiredItem auction : this.expired_items.get(uuid)) {
            if (!auction.getStoreID().equals(store_id)) continue;

            key = auction;

            break;
        }

        return key;
    }

    public final Map<UUID, List<ExpiredItem>> getExpiredItems() {
        return this.expired_items;
    }

    public final boolean hasExpiredItem(final UUID uuid) {
        return this.expired_items.containsKey(uuid);
    }

    public final List<ExpiredItem> getExpiredItems(final UUID uuid) {
        return this.expired_items.get(uuid);
    }

    public void addActiveAuction(final String uuid, final ConfigurationSection section) {
        final AuctionItem new_auction = new AuctionItem(uuid, section.getString("name"),
                section.getName(),
                section.getString("item"),
                section.getString("store_id"),
                section.getLong("price"),
                section.getLong("time.expire"),
                section.getLong("time.full"),
                section.getString("status.top_bidder.uuid", "None"),
                section.getString("status.top_bidder.name", "None"),
                section.getLong("status.top_bidder.price", 0),
                section.getBoolean("status.biddable")
        );

        final UUID fromString = UUID.fromString(uuid);

        if (this.auctions.containsKey(fromString)) {
            final List<AuctionItem> auctions = this.auctions.get(fromString);

            auctions.add(new_auction);

            this.auctions.put(fromString, auctions);
        } else {
            this.auctions.put(fromString, new ArrayList<>() {{
                add(new_auction);
            }});
        }
    }

    /**
     * Adds an item to the configuration section
     *
     * @param itemStack {@link ItemStack}
     * @param price the price
     * @param isBiddable true or false
     * @param config the config.yml
     * @param section the section in the data.yml
     */
    private void addItem(final ItemStack itemStack, final String uuid, final long price, final boolean isBiddable, final FileConfiguration config, final ConfigurationSection section) {
        section.set("item", Methods.toBase64(itemStack));
        section.set("store_id", UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        section.set("price", price);

        if (isBiddable) {
            section.set("time.expire", Methods.convertToMill(config.getString("Settings.Bid-Time", "2m 30s")));
        } else {
            section.set("time.expire", Methods.convertToMill(config.getString("Settings.Sell-Time", "2d")));
        }

        section.set("time.full", Methods.convertToMill(config.getString("Settings.Full-Expire-Time", "10d")));

        section.set("status.top_bidder.uuid", "None");
        section.set("status.top_bidder.name", "None");
        section.set("status.top_bidder.price", 0);
        section.set("status.biddable", isBiddable);

        addActiveAuction(uuid, section);

        Files.data.save();
    }

    private void addExpiredAuction(final String uuid, final ConfigurationSection section) {
        final ExpiredItem expired_item = new ExpiredItem(
                uuid,
                section.getString("name"),
                section.getName(),
                section.getString("item"),
                section.getString("store_id"),
                section.getLong("full_time"),
                section.getLong("price")
        );

        final UUID fromString = UUID.fromString(uuid);

        if (this.expired_items.containsKey(fromString)) {
            final List<ExpiredItem> auctions = this.expired_items.get(fromString);

            auctions.add(expired_item);

            this.expired_items.put(fromString, auctions);
        } else {
            this.expired_items.put(fromString, new ArrayList<>() {{
                add(expired_item);
            }});
        }
    }
}