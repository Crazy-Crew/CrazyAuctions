package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyManager {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    public void load() {

        // Used for user data.
        init();
    }

    public void reload(boolean serverStop) {

        if (!serverStop) {
            // Used for user data.
            init();
        }
    }

    private void init() {

    }
}