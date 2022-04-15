package me.badbones69.crazyauctions.api.placeholders;

import org.bukkit.entity.Player;

public class PlaceholderAPI implements Placeholder {
    public String replace(Player player, String message) {
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
    }
}
