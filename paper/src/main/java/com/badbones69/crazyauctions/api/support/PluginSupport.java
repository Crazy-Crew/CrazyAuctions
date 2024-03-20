package com.badbones69.crazyauctions.api.support;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public enum PluginSupport {

    DECENT_HOLOGRAMS("DecentHolograms"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    VAULT("Vault"),
    CMI("CMI"),
    PLACEHOLDERAPI("PlaceholderAPI"),
    ORAXEN("Oraxen"),
    ITEMS_ADDER("ItemsAdder");

    private final String name;

    private final @NotNull CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    PluginSupport(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isPluginEnabled() {
        return this.plugin.getServer().getPluginManager().isPluginEnabled(this.name);
    }
}