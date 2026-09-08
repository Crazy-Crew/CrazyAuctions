package com.badbones69.crazyauctions.api.builders.gui;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.entity.Player;

public abstract class InventoryBuilder<T extends com.ryderbelserion.fusion.paper.builders.gui.GuiBuilder<T>> {

    protected final CrazyAuctions plugin = CrazyAuctions.get();

    protected final Player player;
    protected final T gui;

    public InventoryBuilder(final Player player, final T gui) {
        this.player = player;
        this.gui = gui;
    }

    public void open() {
        this.gui.open(this.player);
    }
}