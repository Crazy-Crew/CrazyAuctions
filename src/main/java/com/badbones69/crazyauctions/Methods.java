package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.FileManager.Files;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.utils.ItemBuilder;
import com.badbones69.crazyauctions.utils.func.ServerProtocol;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class Methods {

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();
    
    public final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    
    public String color(String message) {
        if (ServerProtocol.isNewer(ServerProtocol.v1_15_R1)) {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer buffer = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
            }

            return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public String getPrefix() {
        return color(Files.CONFIG.getFile().getString("Settings.Prefix", ""));
    }
    
    public String getPrefix(String msg) {
        return color(Files.CONFIG.getFile().getString("Settings.Prefix", "") + msg);
    }

    public String sanitizeColor(String msg) {
        return sanitizeFormat(color(msg));
    }
    
    public String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }

    public static String sanitizeFormat(String string) {
        return TextComponent.toLegacyText(TextComponent.fromLegacyText(string));
    }
    
    public ItemStack makeItem(Material material, int amount, String name, List<String> lore, HashMap<Enchantment, Integer> enchants) {
        ItemBuilder newItem = new ItemBuilder().setMaterial(material).setAmount(amount).setName(name).setLore(lore).setEnchantments(enchants);
        return newItem.build();
    }

    public ItemStack getItemInHand(Player player) {
        if (ServerProtocol.isAtLeast(ServerProtocol.v1_12_R1)) {
            return player.getInventory().getItemInMainHand();
        } else {
            return player.getItemInHand();
        }
    }

    public void setItemInHand(Player player, ItemStack item) {
        if (ServerProtocol.isAtLeast(ServerProtocol.v1_12_R1)) {
            player.getInventory().setItemInMainHand(item);
        } else {
            player.setItemInHand(item);
        }
    }
    
    public Player getPlayer(String name) {
        try {
            return plugin.getServer().getPlayer(name);
        } catch (Exception e) {
            return null;
        }
    }
    
    @SuppressWarnings("deprecation")
    public OfflinePlayer getOfflinePlayer(String name) {
        return plugin.getServer().getOfflinePlayer(name);
    }
    
    public boolean isOnline(String name) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) return true;
        }

        return false;
    }
    
    public boolean isOnline(String name, CommandSender commandSender) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) return true;
        }

        commandSender.sendMessage(Messages.NOT_ONLINE.getMessage());
        return false;
    }
    
    public boolean hasPermission(Player player, String perm) {
        if (!player.hasPermission("crazyauctions." + perm)) {
            player.sendMessage(Messages.NO_PERMISSION.getMessage());
            return false;
        }

        return true;
    }
    
    public boolean hasPermission(CommandSender sender, String perm) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("crazyauctions." + perm)) {
                player.sendMessage(Messages.NO_PERMISSION.getMessage());
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    
    public boolean isInvFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
}