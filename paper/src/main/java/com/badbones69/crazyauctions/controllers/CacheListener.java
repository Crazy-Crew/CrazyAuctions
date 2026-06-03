package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.CrazyPlatform;
import com.badbones69.crazyauctions.api.registry.PaperUserRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final PaperUserRegistry userRegistry = this.platform.getUserRegistry();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.userRegistry.addUser(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.userRegistry.removeUser(event.getPlayer().getUniqueId());
    }
}