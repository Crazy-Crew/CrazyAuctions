package com.badbones69.crazyauctions.api.objects;

import com.badbones69.crazyauctions.api.objects.items.PaperAuctionItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.objects.player.IPlayer;

public class PaperPlayer extends IPlayer<PaperAuctionItem> {

    private final Player player;

    public PaperPlayer(@NotNull final Player player) {
        super(player);

        this.player = player;
    }

    @Override
    public void takeItem(@NotNull final PaperAuctionItem item) {
        final ItemStack itemStack = item.getItem();

        if (itemStack.isEmpty()) return;

        final PlayerInventory inventory = this.player.getInventory();

        inventory.remove(itemStack);
    }

    @Override
    public @NotNull final Player getPlayer() {
        return this.player;
    }
}