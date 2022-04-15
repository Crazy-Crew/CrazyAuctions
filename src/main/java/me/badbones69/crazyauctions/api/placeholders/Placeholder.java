package me.badbones69.crazyauctions.api.placeholders;

import org.bukkit.entity.Player;

public interface Placeholder {
    String replace(Player player, String message);
}
