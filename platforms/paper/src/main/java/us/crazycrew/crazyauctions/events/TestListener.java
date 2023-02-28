package us.crazycrew.crazyauctions.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.crazycrew.crazyauctions.api.interfaces.Universal;
import us.crazycrew.crazyauctions.configs.data.PlayerData;
import java.io.File;

public class TestListener implements Listener, Universal {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerData.load(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        File playerData = PlayerData.getFile(plugin.getUsers(), event.getPlayer().getUniqueId()).toFile();

        PlayerData.save(event.getPlayer().getUniqueId());
    }
}