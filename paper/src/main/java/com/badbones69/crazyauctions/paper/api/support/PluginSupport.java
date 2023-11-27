package com.badbones69.crazyauctions.paper.api.support;

import com.badbones69.crazyauctions.paper.CrazyAuctions;
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

    @NotNull
    private final CrazyAuctions plugin = CrazyAuctions.get();

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