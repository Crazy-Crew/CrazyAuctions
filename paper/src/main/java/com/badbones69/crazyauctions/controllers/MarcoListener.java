package com.badbones69.crazyauctions.controllers;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyauctions.configs.ConfigManager;
import com.badbones69.crazyauctions.configs.impl.ConfigKeys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MarcoListener implements Listener {

    private final SettingsManager config = ConfigManager.getConfig();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!this.config.getProperty(ConfigKeys.macro_dupe_patch)) return;

        if (!player.isOnline() || player.isDead()) event.setCancelled(true);
    }
}