package com.badbones69.crazyauctions;

import org.bukkit.plugin.java.JavaPlugin;

public class CrazyAuctions extends JavaPlugin {

    private static CrazyAuctions plugin;

    public CrazyAuctions() {
        super();

        plugin = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public static CrazyAuctions getPlugin() {
        return plugin;
    }
}