package com.badbones69.crazyauctions.configuration.types;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class Config {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final YamlConfiguration configuration;

    public Config() {
        this.configuration = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "config.yml"));
    }

    public final YamlConfiguration getConfiguration() {
        return this.configuration;
    }
}