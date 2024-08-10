package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.Files;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.ryderbelserion.vital.paper.VitalPaper;
import com.ryderbelserion.vital.paper.files.config.FileManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;

public class CrazyManager extends VitalPaper {

    private final FileManager fileManager;

    private boolean sellingEnabled;
    private boolean biddingEnabled;

    public CrazyManager(final JavaPlugin plugin) {
        super(plugin);

        this.fileManager = new FileManager();

        this.fileManager.addFile("config.yml")
                .addFile("data.yml")
                .addFile("messages.yml")
                .init();
    }

    public void load() {
        this.sellingEnabled = Files.config.getConfiguration().getBoolean("Settings.Feature-Toggle.Selling", true);

        this.biddingEnabled = Files.config.getConfiguration().getBoolean("Settings.Feature-Toggle.Bidding", true);
    }

    public void unload() {
        Files.data.save();
    }
    
    public final boolean isSellingEnabled() {
        return this.sellingEnabled;
    }
    
    public final boolean isBiddingEnabled() {
        return this.biddingEnabled;
    }

    public final FileManager getFileManager() {
        return this.fileManager;
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

    @Override
    public final boolean isLegacy() {
        return true;
    }

    @Override
    public final boolean isVerbose() {
        return true;
    }
}