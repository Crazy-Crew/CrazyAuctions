package com.badbones69.crazyenvoys.storage.impl.types.file;

import com.badbones69.crazyenvoys.enums.Files;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.storage.IStorageHolder;
import java.util.UUID;

public class YamlFactory extends IStorageHolder {

    private final FusionPaper fusion = (FusionPaper) FusionProvider.getInstance();

    private YamlConfiguration configuration;

    public YamlFactory(@NonNull final YamlConfiguration configuration) {
        this.configuration = configuration;
    }

    public @NonNull final YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public @NonNull final YamlFactory init() {
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
                    Files.data.save();
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
                    Files.data.save();
                }
            }
        }

        return this;
    }

    @Override
    public void reload() {
        this.configuration = Files.data.getConfiguration();
    }

    @Override
    public void insertUser(@NonNull final UUID uuid, @NonNull final String name) {}

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
    public boolean hasItem(@NonNull final UUID uuid) {
        return false;
    }

    @Override
    public void stop() {

    }
}