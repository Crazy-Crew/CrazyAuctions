package com.badbones69.crazyauctions.player;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.ryderbelserion.ithildin.core.IthildinCore;
import com.ryderbelserion.ithildin.core.player.PlayerListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PaperPlayerListener implements PlayerListener, Listener {

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoinEvent(PlayerJoinEvent event) {
        PaperPlayerRegistry registry = (PaperPlayerRegistry) IthildinCore.api().getPlayerRegistry();

        // Add the player to the registry.
        String message = "<red> " + event.getPlayer().getName() + "</red> <yellow>has joined.</yellow>";

        onConnect(registry.addPlayer(event.getPlayer()), message, "<red>[" + plugin.getName() + "]</red>");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PaperPlayerRegistry registry = (PaperPlayerRegistry) IthildinCore.api().getPlayerRegistry();

        // Remove the player from the registry.
        String message = "<red> " + event.getPlayer().getName() + "</red> <yellow>has left.</yellow>";

        onDisconnect(registry.removePlayer(event.getPlayer()), message, "<red>[" + plugin.getName() + "]</red>");
    }
}