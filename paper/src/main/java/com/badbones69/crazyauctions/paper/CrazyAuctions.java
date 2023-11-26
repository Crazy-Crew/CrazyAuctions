package com.badbones69.crazyauctions.paper;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.Timer;

public class CrazyAuctions extends JavaPlugin {

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private Timer timer;

    @Override
    public void onEnable() {
        // Instantiate timer
        this.timer = new Timer();
    }

    @Override
    public void onDisable() {
        // Cancel all timers.
        if (this.timer != null) this.timer.cancel();
    }
}