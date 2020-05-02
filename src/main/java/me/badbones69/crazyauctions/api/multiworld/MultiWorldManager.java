package me.badbones69.crazyauctions.api.multiworld;

import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.interfaces.AuctionItem;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MultiWorldManager {
    
    private static MultiWorldManager instance = new MultiWorldManager();
    private boolean enabled;
    private boolean perWorld;
    private List<PerWorld> perWorlds = new ArrayList<>();
    private List<WorldGroup> worldGroups = new ArrayList<>();
    
    //TODO Need to make some kind of grabber for per world auction houses.
    public void load() {
        worldGroups.clear();
        perWorlds.clear();
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
    
    public WorldGroup getWorldGroup(AuctionItem auctionItem) {
        return auctionItem.getWorldGroup();
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
    
    public PerWorld getPerWorld(AuctionItem auctionItem) {
        return auctionItem.getPerWorld();
    }
    
    public PerWorld getPerWorld(World world) {
        return getPerWorld(world.getName());
    }
    
    public PerWorld getPerWorld(String worldString) {
        for (PerWorld perWorld : perWorlds) {
            if (perWorld.getWorldName().equalsIgnoreCase(worldString)) {
                return perWorld;
            }
        }
        return null;
    }
    
    public List<PerWorld> getPerWorlds() {
        return perWorlds;
    }
    
    public static MultiWorldManager getInstance() {
        return instance;
    }
    
}