package com.badbones69.crazyauctions.storage;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import com.badbones69.crazyauctions.api.interfaces.Universal;
import com.badbones69.crazyauctions.storage.data.PlayerData;
import com.badbones69.crazyauctions.data.UserCache;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Description: Handles player data for auctions.
 */
public class StorageManager implements Universal, UserCache {

    /**
     * Add a player to the hashmap if absent.
     *
     * @param uuid player uuid
     */
    @Override
    public void addPlayer(UUID uuid) {
        PlayerData.auctions.putIfAbsent(uuid, "");
    }

    /**
     * Remove the player from the hashmap.
     *
     * @param uuid player uuid
     */
    @Override
    public void removePlayer(UUID uuid) {
        PlayerData.auctions.remove(uuid);
    }

    /**
     * Fetch the player if online or offline.
     *
     * @param uuid player uuid
     * @return player object
     */
    @Override
    public String getPlayerName(UUID uuid) {
        Player player = plugin.getServer().getPlayer(uuid);

        assert player != null;
        if (player.isOnline()) return player.getName();

        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(uuid);

        return offlinePlayer.getName();
    }

    /**
     * Fetch the uuid file of the player.
     *
     * @param path the path i.e. 'CrazyAuctions/userdata/random-uuid.'
     * @param uuid the player uuid
     * @return the complete path
     */
    @Override
    public Path getFile(Path path, UUID uuid) {
        return path.resolve(uuid + ".json");
    }
}