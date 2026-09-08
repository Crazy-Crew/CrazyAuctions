package com.badbones69.crazyauctions.api.builders.gui.types;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.builders.gui.InventoryBuilder;
import com.ryderbelserion.fusion.paper.builders.gui.types.simple.SimpleGui;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class StaticGui extends InventoryBuilder<SimpleGui> {

    public StaticGui(final Player player, final String title, final int rows) {
        super(player, SimpleGui.gui(CrazyAuctions.get(), title, rows));
    }
}