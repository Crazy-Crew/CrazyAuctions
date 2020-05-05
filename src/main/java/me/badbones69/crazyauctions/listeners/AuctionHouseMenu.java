package me.badbones69.crazyauctions.listeners;

import me.badbones69.crazyauctions.api.managers.MenuManager;
import me.badbones69.crazyauctions.api.objects.gui.AuctionHouse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class AuctionHouseMenu implements Listener {
    
    private static MenuManager menuManager = MenuManager.getInstance();
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if (inventory.getHolder() instanceof AuctionHouse) {
            e.setCancelled(true);
        }
    }
    
}