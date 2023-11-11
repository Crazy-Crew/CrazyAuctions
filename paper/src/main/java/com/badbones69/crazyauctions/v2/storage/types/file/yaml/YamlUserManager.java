package com.badbones69.crazyauctions.storage.types.file.yaml;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.frame.storage.enums.StorageType;
import com.badbones69.crazyauctions.storage.interfaces.UserManager;
import com.badbones69.crazyauctions.storage.objects.UserData;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class YamlUserManager extends YamlConfiguration implements UserManager {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private final ConcurrentHashMap<UUID, UserData> userData = new ConcurrentHashMap<>();

    public YamlUserManager() {

    }

    @Override
    public void load(UUID uuid) {
        try {
            File file = new File(this.plugin.getDataFolder() + "/users/" + uuid + ".yml");

            if (!file.exists()) file.createNewFile();

            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveSingular(UUID uuid, boolean serverExit) {
        // If user data empty return.
        if (this.userData.isEmpty()) return;

        // Check if user data contains keys.
        if (this.userData.containsKey(uuid)) {
            // Remove user when done.
            this.userData.remove(uuid);

            // Save the file then load the changes back in.
            reload(uuid, serverExit);
        }
    }

    private void reload(UUID uuid, boolean serverExit) {
        try {
            File file = new File(this.plugin.getDataFolder() + "/users/" + uuid + ".yml");

            save(file);

            if (!serverExit) load(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(boolean serverExit) {
        // If user data empty return.
        if (this.userData.isEmpty()) return;

        // If the player is not leaving, continue here as we are stopping the server or doing periodic save.
        this.userData.forEach((id, user) -> {
            //user.getKeys().forEach((crateMap, keys) -> set("users." + id + "." + crateMap, keys));

            // Save the file then load the changes back in.
            reload(id, serverExit);
        });
    }

    @Override
    public void convert(File file, UUID uuid, StorageType storageType) {

    }

    @Override
    public void addAuction(UUID uuid) {
        Player player = this.plugin.getServer().getPlayer(uuid);
    }

    @Override
    public File getFile(Path path, UUID uuid) {
        return new File(this.plugin.getDataFolder() + "/users/" + uuid + ".yml");
    }

    @Override
    public UserData getUser(UUID uuid) {
        Player player = this.plugin.getServer().getPlayer(uuid);

        // Return with their user data.
        return this.userData.get(uuid);
    }

    @Override
    public Map<UUID, UserData> getUsers() {
        return Collections.unmodifiableMap(this.userData);
    }
}