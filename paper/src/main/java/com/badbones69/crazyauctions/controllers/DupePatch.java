package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.api.FileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class DupePatch implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        boolean macro = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Patches.Macro-Dupe", true);

        if (!macro) return;

        if (!player.isOnline() || player.isDead()) event.setCancelled(true);
    }
}