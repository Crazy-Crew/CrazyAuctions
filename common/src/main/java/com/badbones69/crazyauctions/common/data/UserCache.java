package com.badbones69.crazyauctions.common.data;

import java.nio.file.Path;
import java.util.UUID;

public interface UserCache {

    /**
     * Add a player to the hashmap if absent.
     *
     * @param uuid player uuid
     */
    void addPlayer(final UUID uuid);

    /**
     * Remove the player from the hashmap.
     *
     * @param uuid player uuid
     */
    void removePlayer(final UUID uuid);

    /**
     * Fetch the player if online or offline.
     *
     * @param uuid player uuid
     * @return player object
     */
    String getPlayerName(final UUID uuid);

    /**
     * Fetch the uuid file of the player.
     *
     * @param path the path i.e. 'CrazyAuctions/userdata/random-uuid.'
     * @param uuid the player uuid
     * @return the complete path
     */
    Path getFile(final Path path, UUID uuid);

}