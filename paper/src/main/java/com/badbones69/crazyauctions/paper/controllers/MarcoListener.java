package com.badbones69.crazyauctions.paper.controllers;

import com.badbones69.crazyauctions.paper.api.FileManager.Files;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MarcoListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        boolean macro = Files.CONFIG.getFile().getBoolean("Settings.Patches.Macro-Dupe", true);

        if (!macro) return;

        if (!player.isOnline() || player.isDead()) event.setCancelled(true);
    }
}