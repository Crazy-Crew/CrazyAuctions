package com.badbones69.crazyauctions.tasks;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.tasks.objects.Auction;
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

    private final Map<UUID, List<Auction>> auctions = new HashMap<>();

    public void addAuction(final Player player, final ItemStack itemStack, final long price, final boolean isBiddable) {
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

                addCache(key, auction);
            }
        }
    }

    public final Map<UUID, List<Auction>> getAuctions() {
        return this.auctions;
    }

    public final Auction getAuctionById(final UUID uuid, final String store_id) {
        Auction key = null;

        for (final Auction auction : this.auctions.get(uuid)) {
            if (!auction.getStoreID().equals(store_id)) continue;

            key = auction;

            break;
        }

        return key;
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
        section.set("status.biddable", isBiddable);

        addCache(uuid, section);

        Files.data.save();
    }

    private void addCache(final String uuid, final ConfigurationSection section) {
        final Auction new_auction = new Auction(uuid, section.getString("name"),
                section.getName(),
                section.getString("item"),
                section.getString("store_id"),
                section.getLong("price"),
                section.getLong("time.expire"),
                section.getLong("time.full"),
                section.getString("status.top_bidder.uuid", "None"),
                section.getString("status.top_bidder.name", "None"),
                section.getBoolean("status.biddable")
        );

        final UUID fromString = UUID.fromString(uuid);

        if (this.auctions.containsKey(fromString)) {
            final List<Auction> auctions = this.auctions.get(fromString);

            auctions.add(new_auction);

            this.auctions.put(fromString, auctions);
        } else {
            this.auctions.put(fromString, new ArrayList<>() {{
                add(new_auction);
            }});
        }
    }
}