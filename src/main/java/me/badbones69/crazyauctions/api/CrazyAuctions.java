package me.badbones69.crazyauctions.api;

import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.enums.MenuButtons;
import me.badbones69.crazyauctions.api.managers.AuctionManager;
import me.badbones69.crazyauctions.api.managers.MenuManager;
import me.badbones69.crazyauctions.api.managers.TimeManager;
import me.badbones69.crazyauctions.api.managers.MultiWorldManager;
import me.badbones69.crazyauctions.api.objects.gui.Button;
import me.badbones69.crazyauctions.api.objects.gui.Category;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CrazyAuctions {
    
    private static CrazyAuctions instance = new CrazyAuctions();
    private String prefix;
    private Plugin plugin;
    private List<Category> categories = new ArrayList<>();
    private AuctionManager auctionManager = AuctionManager.getInstance();
    private MultiWorldManager multiWorldManager = MultiWorldManager.getInstance();
    private MenuManager menuManager = MenuManager.getInstance();
    
    public static CrazyAuctions getInstance() {
        return instance;
    }
    
    public void load() {
        plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyAuctions");
        categories.clear();
        FileConfiguration config = Files.CONFIG.getFile();
        prefix = color(config.getString("Settings.Prefix"));
        config.getConfigurationSection("Settings.Categories").getKeys(false).forEach(category -> categories.add(new Category(category)));
        //Load Managers
        TimeManager.load();
        menuManager.load();
        System.out.println("[CrazyAuctions] Loaded Menu Buttons!");
        for (MenuButtons menuButton : MenuButtons.values()) {
            Button button = menuManager.getButton(menuButton);
            System.out.println("[CrazyAuctions] " + menuButton.name() + " Slot:" + button.getSlot() + " Enabled: " + button.isEnabled() + " Item Type:" + button.getItem().getMaterial());
        }
        multiWorldManager.load();
        System.out.println("[CrazyAuctions] Loaded Multi-World Manager!");
        System.out.println("[CrazyAuctions] Is Enabled: " + multiWorldManager.isEnabled());
        System.out.println("[CrazyAuctions] Is Per-World: " + multiWorldManager.isPerWorld());
        System.out.println("[CrazyAuctions] World Groups: " + multiWorldManager.getWorldGroups().size());
        auctionManager.load();
        System.out.println("[CrazyAuctions] Loaded Auctions Items!");
        System.out.println("[CrazyAuctions] Selling Items: " + auctionManager.getSellingItems().size());
        System.out.println("[CrazyAuctions] Bidding Items: " + auctionManager.getBiddingItems().size());
        System.out.println("[CrazyAuctions] Expired Items: " + auctionManager.getExpiredItems().size());
    }
    
    public void saveAuctionHouse() {
        new BukkitRunnable() {
            @Override
            public void run() {
                auctionManager.saveAuctionHouse();
            }
        }.runTaskAsynchronously(plugin);
    }
    
    public Plugin getPlugin() {
        return plugin;
    }
    
    public String getPrefix() {
        return getPrefix("");
    }
    
    public String getPrefix(String message) {
        return prefix + color(message);
    }
    
    public List<Category> getCategories() {
        return categories;
    }
    
    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
}