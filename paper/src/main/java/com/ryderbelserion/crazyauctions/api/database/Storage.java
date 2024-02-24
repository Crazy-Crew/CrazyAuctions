package com.ryderbelserion.crazyauctions.api.database;

import com.ryderbelserion.crazyauctions.CrazyAuctions;
import com.ryderbelserion.crazyauctions.api.database.impl.StorageImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class Storage {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final StorageImpl storage;

    public Storage(StorageImpl storage) {
        this.storage = storage;
    }

    public StorageImpl getStorage() {
        return this.storage;
    }

    public void init() {
        this.storage.start();
    }

    public Connection getConnection() {
        try {
            return getStorage().getConnection();
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to get: " + this.storage.getImplName(), exception);

            return null;
        }
    }

    public void shutdown() {
        try {
            this.storage.shutdown();
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to shutdown: " + this.storage.getImplName(), exception);
        }
    }
}