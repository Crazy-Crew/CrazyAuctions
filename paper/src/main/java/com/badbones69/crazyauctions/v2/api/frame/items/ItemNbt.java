package com.badbones69.crazyauctions.api.frame.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemNbt {

    private final JavaPlugin plugin;

    public ItemNbt(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack setString(ItemStack itemStack, String key, String value) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return null;

        meta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, key), PersistentDataType.STRING, value);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public String getString(ItemStack itemStack, String key) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return null;

        return meta.getPersistentDataContainer().get(new NamespacedKey(this.plugin, key), PersistentDataType.STRING);
    }

    public ItemStack setBoolean(ItemStack itemStack, String key, boolean value) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return null;

        meta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, key), PersistentDataType.BOOLEAN, value);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack removeTag(ItemStack itemStack, String key) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return null;

        meta.getPersistentDataContainer().remove(new NamespacedKey(this.plugin, key));
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}