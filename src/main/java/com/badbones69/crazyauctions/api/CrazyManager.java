package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.api.FileManager.Files;
import com.badbones69.crazyauctions.api.enums.ShopCategories;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;

public class CrazyManager {

    private Boolean sellingEnabled;
    private Boolean biddingEnabled;

    public void load() {
        if (Files.CONFIG.getFile().contains("Settings.Feature-Toggle.Selling")) {
            this.sellingEnabled = Files.CONFIG.getFile().getBoolean("Settings.Feature-Toggle.Selling");
        } else {
            this.sellingEnabled = true;
        }

        if (Files.CONFIG.getFile().contains("Settings.Feature-Toggle.Bidding")) {
            this.biddingEnabled = Files.CONFIG.getFile().getBoolean("Settings.Feature-Toggle.Bidding");
        } else {
            this.biddingEnabled = true;
        }
    }
    
    public Boolean isSellingEnabled() {
        return sellingEnabled;
    }
    
    public Boolean isBiddingEnabled() {
        return biddingEnabled;
    }
    
    public ArrayList<ItemStack> getItems(Player player) {
        FileConfiguration data = Files.DATA.getFile();
        ArrayList<ItemStack> items = new ArrayList<>();

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                if (data.getString("Items." + i + ".Seller").equalsIgnoreCase(player.getName())) items.add(data.getItemStack("Items." + i + ".Item").clone());
            }
        }

        return items;
    }
    
    public ArrayList<ItemStack> getItems(Player player, ShopCategories type) {
        FileConfiguration data = Files.DATA.getFile();
        ArrayList<ItemStack> items = new ArrayList<>();

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                if (data.getString("Items." + i + ".Seller").equalsIgnoreCase(player.getName())) {
                    if (data.getBoolean("Items." + i + ".Biddable")) {
                        if (type == ShopCategories.BID) items.add(data.getItemStack("Items." + i + ".Item").clone());
                    } else {
                        if (type == ShopCategories.SELL) items.add(data.getItemStack("Items." + i + ".Item").clone());
                    }
                }
            }
        }

        return items;
    }
}