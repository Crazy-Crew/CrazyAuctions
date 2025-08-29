package com.badbones69.crazyauctions.v2.api.items;

import com.badbones69.crazyauctions.v2.api.gui.frame.items.GuiItem;
import com.ryderbelserion.fusion.paper.api.builders.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import java.util.ArrayList;

public class AuctionItem {

    private final ItemBuilder itemBuilder;
    private final boolean isActive;

    public AuctionItem(@NotNull final CommentedConfigurationNode node) {
        this.isActive = node.node("active").getBoolean(false);

        final CommentedConfigurationNode item = node.node("item");

        this.itemBuilder = ItemBuilder.from(item.node("type").getString("paper"))
                .setDisplayName(item.node("name").getString(""))
                .setAmount(1);

        try {
            this.itemBuilder.withDisplayLore(item.node("lore").getList(String.class, new ArrayList<>()));
        } catch (final SerializationException exception) {
            //todo() add logging
        }
    }

    public ItemStack asItemStack(@NotNull final Player player) {
        return this.itemBuilder.asItemStack(player);
    }

    public GuiItem asGuiItem(@NotNull final Player player) {
        return new GuiItem(asItemStack(player));
    }

    public GuiItem asGuiItem() {
        return new GuiItem(this.itemBuilder.asItemStack());
    }

    public boolean isActive() {
        return this.isActive;
    }
}