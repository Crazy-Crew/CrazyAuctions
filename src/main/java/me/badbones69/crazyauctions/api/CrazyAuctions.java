package me.badbones69.crazyauctions.api;

import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.enums.MenuButtons;
import me.badbones69.crazyauctions.api.managers.AuctionManager;
import me.badbones69.crazyauctions.api.managers.MenuManager;
import me.badbones69.crazyauctions.api.managers.TimeManager;
import me.badbones69.crazyauctions.api.objects.Button;
import me.badbones69.crazyauctions.api.objects.Category;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CrazyAuctions {
    
    private static CrazyAuctions instance = new CrazyAuctions();
    private String prefix;
    private List<Category> categories = new ArrayList<>();
    private AuctionManager auctionManager = AuctionManager.getInstance();
    private MenuManager menuManager = MenuManager.getInstance();
    
    public static CrazyAuctions getInstance() {
        return instance;
    }
    
    public void load() {
        categories.clear();
        FileConfiguration config = Files.CONFIG.getFile();
        prefix = config.getString("Settings.Prefix");
        config.getConfigurationSection("Settings.Categories").getKeys(false).forEach(category -> categories.add(new Category(category)));
        //Load Managers
        TimeManager.load();
        menuManager.load();
        System.out.println("[CrazyAuctions] Loaded Menu Buttons!");
        for (MenuButtons menuButton : MenuButtons.values()) {
            Button button = menuManager.getButton(menuButton);
            System.out.println("[CrazyAuctions] " + menuButton.name() + " Slot:" + button.getSlot() + " Enabled: " + button.isEnabled() + " Item Type:" + button.getItem().getMaterial());
        }
        auctionManager.loadAuctionHouse();
        System.out.println("[CrazyAuctions] Loaded Auctions Items!");
        System.out.println("[CrazyAuctions] Selling Items: " + auctionManager.getSellingItems().size());
        System.out.println("[CrazyAuctions] Bidding Items: " + auctionManager.getBiddingItems().size());
        System.out.println("[CrazyAuctions] Expired Items: " + auctionManager.getExpiredItems().size());
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