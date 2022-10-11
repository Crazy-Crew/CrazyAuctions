package com.badbones69.crazyauctions.utils.func;

import com.badbones69.crazyauctions.CrazyAuctions;

public enum PluginSupport {
    
    PLACEHOLDERAPI("PlaceholderAPI"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    DECENT_HOLOGRAMS("DecentHolograms"),
    VAULT("Vault");
    
    private final String name;

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();
    
    PluginSupport(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        return plugin.getServer().getPluginManager().getPlugin(name) != null;
    }
}