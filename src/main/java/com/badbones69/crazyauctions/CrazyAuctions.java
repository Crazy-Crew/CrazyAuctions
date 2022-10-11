package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.FileManager;
import com.badbones69.crazyauctions.api.FileManager.Files;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyAuctions extends JavaPlugin implements Listener {

    private static CrazyAuctions plugin;

    private Starter starter;

    private final FileManager fileManager = getStarter().getFileManager();

    private final CrazyManager crazyManager = getStarter().getCrazyManager();

    private final Methods methods = getStarter().getMethods();

    private boolean isEnabled = false;

    @Override
    public void onEnable() {
        try {
            plugin = this;

            starter = new Starter();

            starter.run();

            fileManager.setup();

            crazyManager.load();

            // methods.updateAuction();
        } catch (Exception e) {
            e.printStackTrace();

            isEnabled = false;

            return;
        }

        isEnabled = true;

        enable();
    }

    @Override
    public void onDisable() {
        if (!isEnabled) return;

        disable();
    }

    private void enable() {

    }

    private void disable() {
        Files.DATA.saveFile();
    }

    public static CrazyAuctions getPlugin() {
        return plugin;
    }

    public Starter getStarter() {
        return starter;
    }
}