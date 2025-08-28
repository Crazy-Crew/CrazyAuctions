package com.badbones69.crazyauctions.v2.api.gui.frame.items;

import com.badbones69.crazyauctions.v2.CrazyAuctionsPlus;
import com.badbones69.crazyauctions.v2.api.gui.frame.interfaces.IGui;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class GuiFiller {

    private final CrazyAuctionsPlus plugin = JavaPlugin.getPlugin(CrazyAuctionsPlus.class);

    private final ComponentLogger logger = this.plugin.getComponentLogger();

    private final List<Integer> bottomItems;
    private final List<Integer> topItems;
    private final IGui gui;
    private final int rows;

    public GuiFiller(@NotNull final IGui gui) {
        this.bottomItems = new ArrayList<>();
        this.topItems = new ArrayList<>();
        this.gui = gui;
        this.rows = this.gui.getRows();

        final int size = this.rows * 9;

        for (int i = 9; i > 0; i--) {
            this.bottomItems.add((size) - i);
        }

        for (int i = 0; i < 9; i++) {
            this.topItems.add(i);
        }
    }

    public void fillLower(@NotNull final ItemStack itemStack) {
        final Inventory inventory = this.gui.getInventory();

        for (final int slot : this.bottomItems) {
            inventory.setItem(slot, itemStack);
        }
    }

    public void fillUpper(@NotNull final ItemStack itemStack) {
        if (this.rows <= 2) return; // do not fill top if the rows is 2, because this is a filler item.

        final Inventory inventory = this.gui.getInventory();

        for (final int slot : this.topItems) {
            if (inventory.getItem(slot) != null) continue;

            inventory.setItem(slot, itemStack);
        }
    }

    public void fill(@NotNull final ItemStack upper, @NotNull final ItemStack lower) {
        fillUpper(upper);
        fillLower(lower);
    }
}