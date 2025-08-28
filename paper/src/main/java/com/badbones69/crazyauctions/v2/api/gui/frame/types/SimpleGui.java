package com.badbones69.crazyauctions.v2.api.gui.frame.types;

import com.badbones69.crazyauctions.v2.api.gui.frame.interfaces.IGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SimpleGui extends IGui {

    public SimpleGui(@NotNull final String title, final int rows, @Nullable final Player player, @NotNull final Map<String, String> placeholders) {
        super(title, rows, player, placeholders);
    }

    public SimpleGui(@NotNull final String title, final int rows, @NotNull final Map<String, String> placeholders) {
        this(title, rows, null, placeholders);
    }

    public SimpleGui(@NotNull final String title, final int rows, @NotNull final Player player) {
        this(title, rows, player, new HashMap<>());
    }

    public SimpleGui(@NotNull final String title, final int rows) {
        this(title, rows, null, new HashMap<>());
    }
}