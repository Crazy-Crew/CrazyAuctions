package com.badbones69.crazyauctions.utils;

import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    /**
     * Converts {@link org.bukkit.potion.PotionEffectType} to mojang mapped ids.
     *
     * @param potion the {@link org.bukkit.potion.PotionEffectType} to convert
     * @return the mojang mapped id
     */
    public static String getPotion(String potion) {
        return potion.isEmpty() ? "" : potion.toLowerCase();
    }

    /**
     * Converts an {@link ItemStack} to an {@link ItemBuilder}.
     *
     * @param player {@link Player}
     * @param itemStack the {@link ItemStack}
     * @return the {@link ItemBuilder}
     */
    public static ItemBuilder convertItemStack(Player player, ItemStack itemStack) {
        ItemBuilder itemBuilder = new ItemBuilder(itemStack.getType(), itemStack.getAmount());

        if (player != null) {
            itemBuilder.setPlayer(player);
        }

        return itemBuilder;
    }

    /**
     * Converts an {@link ItemStack} without a {@link Player}.
     *
     * @param itemStack the {@link ItemStack}
     * @return the {@link ItemBuilder}
     */
    public static ItemBuilder convertItemStack(ItemStack itemStack) {
        return convertItemStack(null, itemStack);
    }
}