package us.crazycrew.crazyauctions.configs;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import us.crazycrew.crazyauctions.api.interfaces.Universal;
import us.crazycrew.crazyauctions.configs.data.PlayerData;
import us.crazycrew.crazyauctions.data.UserCache;
import java.nio.file.Path;
import java.util.UUID;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Created: 2/28/2023
 * Time: 3:04 AM
 * Last Edited: 2/28/2023 @ 3:11 AM
 *
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