package com.badbones69.crazyauctions.utils;

import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import java.util.ArrayList;
import java.util.List;

public class AuctionUtils {

    public static List<ItemBuilder> getItems(final ConfigurationSection section) {
        if (section == null) return List.of();

        final List<ItemBuilder> itemStacks = new ArrayList<>();

        for (final String key : section.getKeys(false)) {
            itemStacks.add(new ItemBuilder().fromBase64(section.getString(key + ".Item", "")));
        }

        return itemStacks;
    }
}