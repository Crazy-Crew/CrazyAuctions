package com.badbones69.crazyauctions.v2.api.gui.frame.items;

import com.badbones69.crazyauctions.v2.api.gui.frame.interfaces.IGuiAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiItem {

    private IGuiAction<InventoryClickEvent> action;
    private final ItemStack itemStack;

    public GuiItem(@NotNull final ItemStack itemStack, @Nullable final IGuiAction<InventoryClickEvent> action) {
        this.itemStack = itemStack;

        if (action != null) {
            this.action = action;
        }
    }

    public GuiItem(@NotNull final ItemType itemType, final int amount, @Nullable final IGuiAction<InventoryClickEvent> action) {
        this(itemType.createItemStack(amount), action);
    }

    public GuiItem(@NotNull final ItemType itemType, @Nullable final IGuiAction<InventoryClickEvent> action) {
        this(itemType, 1, action);
    }

    public GuiItem(@NotNull final ItemType itemType) {
        this(itemType, 1, null);
    }

    public GuiItem(@NotNull final ItemStack itemStack) {
        this(itemStack, null);
    }

    public void setAction(@NotNull final IGuiAction<InventoryClickEvent> action) {
        this.action = action;
    }

    public @Nullable IGuiAction<InventoryClickEvent> getAction() {
        return this.action;
    }

    public @NotNull ItemStack getItemStack() {
        return this.itemStack;
    }
}