package com.badbones69.crazyauctions.events;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.storage.interfaces.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DataListener implements Listener {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private final UserManager userManager = this.plugin.getCrazyManager().getStorageManager().getUserManager();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.userManager.load(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.userManager.saveSingular(event.getPlayer().getUniqueId(), true);
    }
}