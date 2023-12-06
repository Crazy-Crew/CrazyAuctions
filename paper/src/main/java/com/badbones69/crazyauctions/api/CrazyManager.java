package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.api.FileManager.Files;
import com.badbones69.crazyauctions.api.enums.ShopType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;

public class CrazyManager {

    private boolean sellingEnabled;
    private boolean biddingEnabled;

    public void load() {
        this.sellingEnabled = Files.CONFIG.getFile().getBoolean("Settings.Feature-Toggle.Selling", true);

        this.biddingEnabled = Files.CONFIG.getFile().getBoolean("Settings.Feature-Toggle.Bidding", true);
    }

    public void unload() {
        Files.DATA.saveFile();
    }
    
    public boolean isSellingEnabled() {
        return sellingEnabled;
    }
    
    public boolean isBiddingEnabled() {
        return biddingEnabled;
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