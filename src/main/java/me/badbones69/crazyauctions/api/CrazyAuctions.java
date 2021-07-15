package me.badbones69.crazyauctions.api;

import me.badbones69.crazyauctions.api.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CrazyAuctions {
    
    private static CrazyAuctions instance = new CrazyAuctions();
    private FileManager fileManager = FileManager.getInstance();
    private Boolean sellingEnabled;
    private Boolean biddingEnabled;
    
    public static CrazyAuctions getInstance() {
        return instance;
    }
    
    public void loadCrazyAuctions() {
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
                if (data.getString("Items." + i + ".Seller").equalsIgnoreCase(player.getName())) {
                    items.add(data.getItemStack("Items." + i + ".Item").clone());
                }
            }
        }
        return items;
    }
    
    public ArrayList<ItemStack> getItems(Player player, ShopType type) {
        FileConfiguration data = Files.DATA.getFile();
        ArrayList<ItemStack> items = new ArrayList<>();
        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                if (data.getString("Items." + i + ".Seller").equalsIgnoreCase(player.getName())) {
                    if (data.getBoolean("Items." + i + ".Biddable")) {
                        if (type == ShopType.BID) {
                            items.add(data.getItemStack("Items." + i + ".Item").clone());
                        }
                    } else {
                        if (type == ShopType.SELL) {
                            items.add(data.getItemStack("Items." + i + ".Item").clone());
                        }
                    }
                }
            }
        }
        return items;
    }
    
}