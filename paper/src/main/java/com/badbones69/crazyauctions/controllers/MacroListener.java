package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.api.enums.Files;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MacroListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        boolean macro = Files.config.getConfiguration().getBoolean("Settings.Patches.Macro-Dupe", true);

        if (!macro) return;

        if (!player.isOnline() || player.isDead()) event.setCancelled(true);
    }
}