package me.badbones69.crazyauctions.api;

import me.badbones69.crazyauctions.api.FileManager.Files;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class CrazyAuctions {
    
    private static CrazyAuctions instance = new CrazyAuctions();
    private String prefix;
    
    public static CrazyAuctions getInstance() {
        return instance;
    }
    
    public void load() {
        FileConfiguration config = Files.CONFIG.getFile();
        prefix = config.getString("Settings.Prefix");
    }
    
    public String getPrefix() {
        return getPrefix("");
    }
    
    public String getPrefix(String message) {
        return prefix + color(message);
    }
    
    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
}