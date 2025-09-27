package com.badbones69.crazyauctions.api.support;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.UUID;

/**
 * A library for the Bukkit API to create player skulls
 * from names, base64 strings, and texture URLs.
 * Does not use any NMS code, and should work across all versions.
 *
 * @author Dean B on 12/28/2016.
 */
public class SkullCreator {

    private static final @NotNull CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);
    
    /**
     * Creates a player skull with a UUID. 1.13 only.
     *
     * @param id The Player's UUID
     * @return The head of the Player
     */
    public static ItemStack itemFromUuid(UUID id) {
        ItemStack item = getPlayerSkullItem();
        
        return itemWithUuid(item, id);
    }
    
    /**
     * Creates a player skull based on a UUID. 1.13 only.
     *
     * @param item The item to apply the name to
     * @param id The Player's UUID
     * @return The head of the Player
     */
    public static ItemStack itemWithUuid(ItemStack item, UUID id) {
        notNull(item, "item");
        notNull(id, "id");
        
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(plugin.getServer().getOfflinePlayer(id));
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Creates a player skull based on a Mojang server URL.
     *
     * @param url The URL of the Mojang skin
     * @return The head associated with the URL
     */
    public static ItemStack itemFromUrl(String url) {
        ItemStack item = getPlayerSkullItem();
        
        return itemWithUrl(item, url);
    }
    
    /**
     * Creates a player skull based on a Mojang server URL.
     *
     * @param item The item to apply the skin to
     * @param url The URL of the Mojang skin
     * @return The head associated with the URL
     */
    public static ItemStack itemWithUrl(ItemStack item, String url) {
        notNull(item, "item");
        notNull(url, "url");
        
        return itemWithBase64(item, urlToBase64(url));
    }
    
    /**
     * Creates a player skull based on a base64 string containing the link to the skin.
     *
     * @param base64 The base64 string containing the texture
     * @return The head with a custom texture
     */
    public static ItemStack itemFromBase64(String base64) {
        ItemStack item = getPlayerSkullItem();

        return itemWithBase64(item, base64);
    }
    
    /**
     * Applies the base64 string to the ItemStack.
     *
     * @param item The ItemStack to put the base64 onto
     * @param base64 The base64 string containing the texture
     * @return The head with a custom texture
     */
    public static ItemStack itemWithBase64(ItemStack item, String base64) {
        notNull(item, "item");
        notNull(base64, "base64");
        
        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        return plugin.getServer().getUnsafe().modifyItemStack(item,
        "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}"
        );
    }
    
    /**
     * Sets the block to a skull with the given UUID.
     *
     * @param block The block to set
     * @param id The player to set it to
     */
    public static void blockWithUuid(Block block, UUID id) {
        notNull(block, "block");
        notNull(id, "id");
        
        setBlockType(block);
        ((Skull) block.getState()).setOwningPlayer(Bukkit.getOfflinePlayer(id));
    }
    
    /**
     * Sets the block to a skull with the given UUID.
     *
     * @param block The block to set
     * @param url The mojang URL to set it to use
     */
    public static void blockWithUrl(Block block, String url) {
        notNull(block, "block");
        notNull(url, "url");
        
        blockWithBase64(block, urlToBase64(url));
    }
    
    /**
     * Sets the block to a skull with the given UUID.
     *
     * @param block The block to set
     * @param base64 The base64 to set it to use
     */
    public static void blockWithBase64(Block block, String base64) {
        notNull(block, "block");
        notNull(base64, "base64");
        
        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        
        String args = String.format(
        "%d %d %d %s",
        block.getX(),
        block.getY(),
        block.getZ(),
        "{Owner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}"
        );

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "data merge block " + args);
    }
    
    private static ItemStack getPlayerSkullItem() {
        return new ItemStack(Material.PLAYER_HEAD);
    }
    
    private static void setBlockType(Block block) {
        block.setType(Material.PLAYER_HEAD, false);
    }
    
    private static void notNull(Object instance, String name) {
        if (instance == null) {
            throw new NullPointerException(name + " should not be null!");
        }
    }
    
    private static String urlToBase64(String url) {
        URI actualUrl;

        try {
            actualUrl = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl + "\"}}}";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }
}