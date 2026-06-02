package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class CrazyManager {

    private boolean sellingEnabled;
    private boolean biddingEnabled;

    public void load() {
        final YamlConfiguration configuration = Files.config.getConfiguration();

        this.sellingEnabled = configuration.getBoolean("Settings.Feature-Toggle.Selling", true);

        this.biddingEnabled = configuration.getBoolean("Settings.Feature-Toggle.Bidding", true);
    }

    public void unload() {
        Files.data.save();
    }
    
    public boolean isSellingEnabled() {
        return this.sellingEnabled;
    }
    
    public boolean isBiddingEnabled() {
        return this.biddingEnabled;
    }
    
    public List<ItemStack> getItems(Player player) {
        final YamlConfiguration data = Files.data.getConfiguration();

        List<ItemStack> items = new ArrayList<>();

        final ConfigurationSection section = data.getConfigurationSection("Items");

        if (section == null) return items;

        final String uuid = player.getUniqueId().toString();

        for (final String id : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(id);

            if (item == null) continue;

            final String seller = item.getString("Seller", "");

            if (seller.isBlank()) continue;

            if (!seller.equals(uuid)) continue;

            final String base64 = item.getString("Item", "");

            if (base64.isBlank()) continue;

            items.add(Methods.fromBase64(base64));
        }

        return items;
    }
}