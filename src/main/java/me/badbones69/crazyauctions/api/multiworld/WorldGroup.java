package me.badbones69.crazyauctions.api.multiworld;

import java.util.ArrayList;
import java.util.List;

public class WorldGroup {
    
    private String id;
    private List<String> worlds = new ArrayList<>();
    
    public WorldGroup(String id, List<String> worlds) {
        this.id = id;
        this.worlds.addAll(worlds);
    }
    
    public String getID() {
        return id;
    }
    
    public List<String> getWorlds() {
        return worlds;
    }
    
}