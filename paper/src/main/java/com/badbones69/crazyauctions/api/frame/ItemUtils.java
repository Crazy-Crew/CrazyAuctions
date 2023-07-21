package com.badbones69.crazyauctions.api.frame;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    private final Material skull = Material.PLAYER_HEAD;

    public ItemStack skull() {
        return new ItemStack(skull);
    }

    public boolean isPlayerSkull(Material itemStack) {
        return itemStack != skull;
    }

    private String getVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        return version.substring(version.lastIndexOf('.') + 1);
    }

    public Class<?> craftClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
    }
}