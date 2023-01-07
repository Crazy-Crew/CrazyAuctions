package com.badbones69.crazyauctions.player;

import com.ryderbelserion.ithildin.core.player.PlayerObject;
import com.ryderbelserion.ithildin.core.player.PlayerRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperPlayerRegistry extends PlayerRegistry {

    @Nullable
    public PlayerObject addPlayer(@NotNull Player player) {
        return register(new PaperPlayer(player));
    }

    @Nullable
    public PlayerObject removePlayer(@NotNull Player player) {
        PlayerObject checkPlayer = get(player.getUniqueId());
        return checkPlayer == null ? null : unregister(checkPlayer);
    }

    public PlayerObject get(@NotNull Player player) {
        return get(player.getUniqueId());
    }
}