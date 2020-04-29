package me.badbones69.crazyauctions.api.multiworld;

import me.badbones69.crazyauctions.api.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MultiWorldManager {
    
    private static MultiWorldManager instance = new MultiWorldManager();
    private boolean enabled;
    private boolean perWorld;
    private List<WorldGroup> worldGroups = new ArrayList<>();
    
    public void load() {
        worldGroups.clear();
        FileConfiguration config = Files.CONFIG.getFile();
        String path = "Settings.Multi-World.";
        enabled = config.getBoolean(path + "Enabled");
        perWorld = config.getBoolean(path + "Per-World");
        if (!perWorld) {
            if (config.contains(path + "World-Groups")) {
                for (String id : config.getConfigurationSection(path + "World-Groups").getKeys(false)) {
                    worldGroups.add(new WorldGroup(id, config.getStringList(path + "World-Groups." + id)));
                }
            }
            //Per World is set to true if no world groups are found.
            if (worldGroups.isEmpty()) {
                perWorld = true;
            }
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean isPerWorld() {
        return perWorld;
    }
    
    public WorldGroup getWorldGroup(String id) {
        for (WorldGroup worldGroup : worldGroups) {
            if (worldGroup.getID().equalsIgnoreCase(id)) {
                return worldGroup;
            }
        }
        return null;
    }
    
    public List<WorldGroup> getWorldGroups() {
        return worldGroups;
    }
    
    public static MultiWorldManager getInstance() {
        return instance;
    }
    
}