package com.badbones69.crazyauctions.datafixer;

import com.badbones69.crazyauctions.common.enums.FileKey;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigFixer {

    public void onEnable() {
        fixRefreshTypo();
    }

    private void fixRefreshTypo() {
        final YamlConfiguration config = FileKey.config.getConfiguration();

        if (config.contains("Settings.GUISettings.OtherSettings.Refresh")) return;

        if (config.contains("Settings.GUISettings.OtherSettings.Refesh")) {
            config.set("Settings.GUISettings.OtherSettings.Refresh", config.get("Settings.GUISettings.OtherSettings.Refesh"));

            FileKey.config.save();
        }
    }
}