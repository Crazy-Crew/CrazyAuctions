package com.badbones69.crazyauctions.storage.objects;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserData {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private final UUID uuid;

    private ConcurrentHashMap<String, String> auctionData = new ConcurrentHashMap<>();

    public UserData(UUID uuid) {
        this.uuid = uuid;
    }

    public Player getPlayer() {
        return this.plugin.getServer().getPlayer(this.uuid);
    }
}