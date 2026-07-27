package com.badbones69.crazyauctions.common.storage.impl.file.types;

import com.badbones69.crazyauctions.common.CrazyAuctionsPlugin;
import com.badbones69.crazyauctions.common.enums.keys.FileKeys;
import com.badbones69.crazyauctions.common.storage.impl.file.FlatFactory;
import com.badbones69.crazyauctions.common.utils.TimeUtils;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.enums.ShopType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class YamlFactory extends FlatFactory {

    private final FusionPaper fusion = (FusionPaper) FusionProvider.getInstance();

    public YamlFactory(final CrazyAuctionsPlugin plugin) {
        super(plugin, "yaml");
    }

    private YamlConfiguration configuration;

    public @NonNull final YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public final void init() {
        this.configuration = FileKeys.data.getConfiguration();

        final ConfigurationSection cancelled = this.configuration.getConfigurationSection("OutOfTime/Cancelled");

        final Server server = this.fusion.getServer();

        if (cancelled != null) {
            for (final String key : cancelled.getKeys(false)) {
                final ConfigurationSection section = cancelled.getConfigurationSection(key);

                if (section == null) continue;

                final String item = section.getString("Item", "");

                if (item.isBlank()) continue;

                final ItemStack itemStack = section.getItemStack("Item", ItemType.AIR.createItemStack());

                boolean isSaving = false;

                if (!itemStack.isEmpty()) {
                    section.set("Item", ItemUtils.toBase64(itemStack));

                    isSaving = true;
                }

                final String seller = section.getString("Seller", "");

                if (!seller.isBlank()) {
                    final OfflinePlayer player = server.getOfflinePlayer(UUID.fromString(seller));

                    section.set("Seller", player.getUniqueId().toString());
                }

                if (isSaving) {
                    FileKeys.data.save();
                }
            }
        }

        final ConfigurationSection items = this.configuration.getConfigurationSection("Items");

        if (items != null) {
            for (String key : items.getKeys(false)) {
                final ConfigurationSection section = items.getConfigurationSection(key);

                if (section == null) continue;

                final String item = section.getString("Item", "");

                if (item.isBlank()) continue;

                final ItemStack itemStack = section.getItemStack("Item", ItemType.AIR.createItemStack());

                boolean isSaving = false;

                if (!itemStack.isEmpty()) {
                    section.set("Item", ItemUtils.toBase64(itemStack));

                    isSaving = true;
                }

                final String seller = section.getString("Seller", "");

                if (!seller.isBlank()) {
                    final OfflinePlayer player = server.getOfflinePlayer(UUID.fromString(seller));

                    final String id = player.getUniqueId().toString();

                    if (!seller.equals(id)) {
                        section.set("Seller", id);

                        isSaving = true;
                    }
                }

                final String bidder = section.getString("TopBidder", "None");

                if (!bidder.isBlank() && !bidder.equalsIgnoreCase("None")) {
                    final OfflinePlayer player = server.getOfflinePlayer(UUID.fromString(bidder));

                    final String id = player.getUniqueId().toString();

                    if (!bidder.equals(id)) {
                        section.set("TopBidder", id);

                        isSaving = true;
                    }
                }

                if (isSaving) {
                    FileKeys.data.save();
                }
            }
        }

        repairStoreIDs();
    }

    /**
     * Gives every listing an id that no other listing shares.
     *
     * <p>The id of an expired item used to be copied over as a number, which came out as 0 for
     * every entry and left the menus unable to tell those listings apart.
     */
    private void repairStoreIDs() {
        final Set<String> ids = new HashSet<>();

        boolean isSaving = false;

        for (final String path : List.of("Items", "OutOfTime/Cancelled")) {
            final ConfigurationSection section = this.configuration.getConfigurationSection(path);

            if (section == null) continue;

            for (final String key : section.getKeys(false)) {
                final ConfigurationSection listing = section.getConfigurationSection(key);

                if (listing == null) continue;

                final String id = listing.getString("StoreID", "");

                if (!id.isBlank() && ids.add(id)) continue;

                String replacement;

                do {
                    replacement = UUID.randomUUID().toString().substring(0, 8);
                } while (!ids.add(replacement));

                listing.set("StoreID", replacement);

                isSaving = true;
            }
        }

        if (isSaving) {
            FileKeys.data.save();
        }
    }

    @Override
    public void reload() {
        this.configuration = FileKeys.data.getConfiguration();
    }

    @Override
    public void addItem(
            @NonNull final UUID uuid,
            @NonNull final String name,
            @NonNull final String base64,
            final long price,
            @NonNull final ShopType shopType
    ) {
        int number = 1;

        final ConfigurationSection section = this.configuration.getConfigurationSection("Items");

        if (section == null) {
            return;
        }

        while (section.contains("%s".formatted(number))) number++;

        final ConfigurationSection item = section.createSection("%s".formatted(number));

        final FileConfiguration config = FileKeys.config.getConfiguration();

        item.set("Seller", uuid.toString());
        item.set("SellerName", name);

        switch (shopType) {
            case BID -> {
                item.set("Time-Till-Expire", TimeUtils.convertToMill(config.getString("Settings.Bid-Time", "2m 30s")));

                item.set("Biddable", true);
            }

            case SELL -> {
                item.set("Time-Till-Expire", TimeUtils.convertToMill(config.getString("Settings.Sell-Time", "2d")));

                item.set("Biddable", false);
            }
        }

        item.set("StoreID", getStoreID(section));

        item.set("Full-Time", TimeUtils.convertToMill(config.getString("Settings.Full-Expire-Time", "10d")));

        item.set("TopBidder", "None");
        item.set("TopBidderName", "None");

        item.set("Price", price);

        item.set("Item", base64);

        FileKeys.data.save();
    }

    /**
     * Builds an id that is not in use by another listing.
     *
     * <p>The menus resolve a clicked item back to its listing by this id, so a duplicate would
     * hand the buyer someone else's item.
     *
     * @param items The section holding the currently listed items.
     * @return An id no other listing is using.
     */
    private String getStoreID(@NonNull final ConfigurationSection items) {
        final ConfigurationSection expired = this.configuration.getConfigurationSection("OutOfTime/Cancelled");

        String id;

        do {
            id = UUID.randomUUID().toString().substring(0, 8);
        } while (isTaken(items, id) || (expired != null && isTaken(expired, id)));

        return id;
    }

    private boolean isTaken(@NonNull final ConfigurationSection section, @NonNull final String id) {
        for (final String key : section.getKeys(false)) {
            final ConfigurationSection listing = section.getConfigurationSection(key);

            if (listing == null) continue;

            if (id.equals(listing.getString("StoreID", key))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasExpiredItem(@NonNull final UUID uuid) {
        final ConfigurationSection section = this.configuration.getConfigurationSection("OutOfTime/Cancelled");

        if (section == null) {
            this.fusion.log(Level.WARNING, "The configuration section required in data.yml is not present!");

            return false;
        }

        boolean hasPlayer = false;

        final String asString = uuid.toString();

        for (final String id : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(id);

            if (item == null) {
                this.fusion.log(Level.WARNING, "Item cannot be null for id %s!", id);

                continue;
            }

            final String seller = item.getString("Seller", "");

            if (seller.isBlank()) {
                this.fusion.log(Level.WARNING, "The seller for id %s cannot be blank!", id);

                continue;
            }

            hasPlayer = seller.equals(asString);

            if (hasPlayer) {
                break;
            }
        }

        return hasPlayer;
    }

    @Override
    public void stop() {

    }

    @Override
    public void save() {

    }
}