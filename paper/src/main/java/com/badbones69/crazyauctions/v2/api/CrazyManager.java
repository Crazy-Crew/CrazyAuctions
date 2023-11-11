package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.storage.types.StorageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyManager {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private StorageManager storageManager;

    public void load(boolean serverStart) {
        if (serverStart) {
            this.storageManager = new StorageManager();

            this.storageManager.init();
        }
    }

    public void reload() {
        if (this.plugin.getApiManager() != null) this.plugin.getApiManager().reload();

        if (this.storageManager.getUserManager() != null) this.storageManager.getUserManager().save(false);
    }

    public void stop() {

    }

    public StorageManager getStorageManager() {
        return this.storageManager;
    }
}