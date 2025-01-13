package com.badbones69.crazyauctions.datafixer;

import com.badbones69.crazyauctions.api.enums.Files;
import org.bukkit.configuration.file.FileConfiguration;


public class ConfigFixer {

    public void onEnable() {
        fixRefreshTypo();
    }

    private void fixRefreshTypo() {
        FileConfiguration config = Files.config.getConfiguration();

        if (config.contains("Settings.GUISettings.OtherSettings.Refresh")) return;

        if (config.contains("Settings.GUISettings.OtherSettings.Refesh")) {

            config.set("Settings.GUISettings.OtherSettings.Refresh", config.get("Settings.GUISettings.OtherSettings.Refesh"));

            Files.config.save();
        }
    }

}
