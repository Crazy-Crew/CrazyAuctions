package com.badbones69.crazyauctions.configs.convert;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class ConfigConversion {

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();

    public void convertConfig() {
        File file = new File(this.plugin.getDataFolder() + "/config.yml");

        File secondFile = new File(this.plugin.getDataFolder() + "/config-v1.yml");

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

        if (yamlConfiguration.getString("Settings.Config-Version") == null && !secondFile.exists()) {
            this.plugin.getLogger().warning("Could not find Config-Version, I am assuming configurations have been converted.");
            return;
        }

        if (file.renameTo(secondFile)) this.plugin.getLogger().warning("Renamed " + file.getName() + " to config-v1.yml");

        YamlConfiguration secondConfiguration = YamlConfiguration.loadConfiguration(secondFile);

        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}