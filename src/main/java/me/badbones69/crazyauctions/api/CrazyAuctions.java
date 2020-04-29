package me.badbones69.crazyauctions.api;

import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.managers.TimeManager;
import me.badbones69.crazyauctions.api.objects.Category;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CrazyAuctions {
    
    private static CrazyAuctions instance = new CrazyAuctions();
    private String prefix;
    private List<Category> categories = new ArrayList<>();
    
    public static CrazyAuctions getInstance() {
        return instance;
    }
    
    public void load() {
        categories.clear();
        FileConfiguration config = Files.CONFIG.getFile();
        prefix = config.getString("Settings.Prefix");
        config.getConfigurationSection("Settings.Categories").getKeys(false).forEach(category -> categories.add(new Category(category)));
        //Load Managers
        TimeManager.load();
    }
    
    public String getPrefix() {
        return getPrefix("");
    }
    
    public String getPrefix(String message) {
        return prefix + color(message);
    }
    
    public List<Category> getCategories() {
        return categories;
    }
    
    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
}