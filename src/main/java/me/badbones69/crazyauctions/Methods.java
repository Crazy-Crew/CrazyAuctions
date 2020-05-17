package me.badbones69.crazyauctions;

import me.badbones69.crazyauctions.api.enums.Version;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Methods {
    
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
    public static ItemStack getItemInHand(Player player) {
        if (Version.isNewer(Version.v1_8_R3)) {
            return player.getInventory().getItemInMainHand();
        } else {
            return player.getItemInHand();
        }
    }
    
}