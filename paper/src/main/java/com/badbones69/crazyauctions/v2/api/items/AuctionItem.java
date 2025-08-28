package com.badbones69.crazyauctions.v2.api.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class AuctionItem {

    public AuctionItem(@NotNull final CommentedConfigurationNode node) {

    }

    public ItemStack asItemStack() {
        return ItemType.ACACIA_BOAT.createItemStack();
    }
}