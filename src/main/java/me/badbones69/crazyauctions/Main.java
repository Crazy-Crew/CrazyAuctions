package me.badbones69.crazyauctions;

import me.badbones69.crazyauctions.api.CrazyAuctions;
import me.badbones69.crazyauctions.api.FileManager;
import me.badbones69.crazyauctions.api.managers.AuctionManager;
import me.badbones69.crazyauctions.commands.CACommand;
import me.badbones69.crazyauctions.listeners.AuctionHouseMenu;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    private CrazyAuctions ca = CrazyAuctions.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    
    @Override
    public void onEnable() {
        fileManager.logInfo(true).setup(this);
        ca.load();
        registerCommands();
        registerListeners();
    }
    
    @Override
    public void onDisable() {
        AuctionManager.getInstance().saveAuctionHouse();
    }
    
    private void registerCommands() {
        getCommand("crazyauctions").setExecutor(new CACommand());
    }
    
    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AuctionHouseMenu(), this);
    }
    
}