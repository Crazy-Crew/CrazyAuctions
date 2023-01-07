package com.badbones69.crazyauctions.player;

import com.ryderbelserion.ithildin.core.player.PlayerObject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.UUID;

public class PaperPlayer extends PlayerObject {

    private final Player player;

    public PaperPlayer(@NotNull Player player) {
        super(createKey(player.getUniqueId()));

        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public UUID getIdentity() {
        return this.player.getUniqueId();
    }

    @Override
    public boolean equals(@Nullable Object instance) {
        if (this == instance) return true;
        if (instance == null) return false;
        if (this.getClass() != instance.getClass()) return false;

        PaperPlayer other = (PaperPlayer) instance;
        return getKey() == other.getKey() && this.player == other.player;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), this.player);
    }

    @Override
    public String toString() {
        return "PaperPlayer{" + "key=" + getKey() + ",player=" + getPlayer().getUniqueId() + "}";
    }
}