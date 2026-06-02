package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyenvoys.CrazyPlugin;
import com.badbones69.crazyenvoys.enums.Files;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CrazyPlatform extends CrazyPlugin<CommandSender> {

    private boolean isSellingModuleEnabled;
    private boolean isBiddingModuleEnabled;

    public CrazyPlatform(@NonNull final FusionPaper fusion) {
        super(fusion);
    }

    @Override
    public void init() {
        super.init();

        final YamlConfiguration configuration = Files.config.getConfiguration();

        this.isSellingModuleEnabled = configuration.getBoolean("Settings.Feature-Toggle.Selling", true);

        this.isBiddingModuleEnabled = configuration.getBoolean("Settings.Feature-Toggle.Bidding", true);
    }

    @Override
    public void reload() {
        super.reload();

        final YamlConfiguration configuration = Files.config.getConfiguration();

        this.isSellingModuleEnabled = configuration.getBoolean("Settings.Feature-Toggle.Selling", true);

        this.isBiddingModuleEnabled = configuration.getBoolean("Settings.Feature-Toggle.Bidding", true);
    }

    @Override
    public void stop() {
        Files.data.save();
    }

    public @NonNull final List<ItemStack> getItems(@NonNull final Player player) {
        final YamlConfiguration data = Files.data.getConfiguration();

        final List<ItemStack> items = new ArrayList<>();

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

    @Override
    public boolean isSellModuleEnabled() {
        return isSellingModuleEnabled;
    }

    @Override
    public boolean isBidModuleEnabled() {
        return isBiddingModuleEnabled;
    }
}