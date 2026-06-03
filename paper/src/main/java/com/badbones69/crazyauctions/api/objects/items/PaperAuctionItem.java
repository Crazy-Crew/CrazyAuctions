package com.badbones69.crazyauctions.api.objects.items;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.items.IAuctionItem;

public class PaperAuctionItem implements IAuctionItem<ItemStack> {

    private final ItemStack itemStack;

    public PaperAuctionItem(@NonNull final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public @NonNull final ItemStack getItem() {
        return this.itemStack;
    }
}