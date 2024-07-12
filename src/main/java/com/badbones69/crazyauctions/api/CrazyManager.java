package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.Files;
import com.badbones69.crazyauctions.api.enums.ShopType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;

public class CrazyManager {

    private boolean sellingEnabled;
    private boolean biddingEnabled;

    public void load() {
        this.sellingEnabled = Files.config.getConfiguration().getBoolean("Settings.Feature-Toggle.Selling", true);

        this.biddingEnabled = Files.config.getConfiguration().getBoolean("Settings.Feature-Toggle.Bidding", true);
    }

    public void unload() {
        Files.data.save();
    }
    
    public boolean isSellingEnabled() {
        return sellingEnabled;
    }
    
    public boolean isBiddingEnabled() {
        return biddingEnabled;
    }
    
    public ArrayList<ItemStack> getItems(Player player, ShopType type) {
        FileConfiguration data = Files.data.getConfiguration();
        ArrayList<ItemStack> items = new ArrayList<>();

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                if (data.getString("Items." + i + ".Seller").equalsIgnoreCase(player.getUniqueId().toString())) {
                    if (data.getBoolean("Items." + i + ".Biddable")) {
                        if (type == ShopType.BID) {
                            items.add(Methods.fromBase64(data.getString("Items." + i + ".Item")));
                        }
                    } else {
                        if (type == ShopType.SELL) {
                            items.add(Methods.fromBase64(data.getString("Items." + i + ".Item")));
                        }
                    }
                }
            }
        }

        return items;
    }
}