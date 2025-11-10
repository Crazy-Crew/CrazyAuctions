package com.badbones69.crazyauctions.api.objects.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.objects.items.IAuctionItem;

public class PaperAuctionItem implements IAuctionItem<ItemStack> {

    private final ItemStack itemStack;

    public PaperAuctionItem() {
        this.itemStack = ItemStack.empty();
    }

    @Override
    public @NotNull final ItemStack getItem() {
        return this.itemStack;
    }
}