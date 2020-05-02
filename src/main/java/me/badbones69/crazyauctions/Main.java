package me.badbones69.crazyauctions;

import me.badbones69.crazyauctions.api.CrazyAuctions;
import me.badbones69.crazyauctions.api.FileManager;
import me.badbones69.crazyauctions.api.managers.AuctionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    private CrazyAuctions ca = CrazyAuctions.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    
    @Override
    public void onEnable() {
        fileManager.logInfo(true).setup(this);
        ca.load();
    }
    
    @Override
    public void onDisable() {
        AuctionManager.getInstance().saveAuctionHouse();
    }
    
}