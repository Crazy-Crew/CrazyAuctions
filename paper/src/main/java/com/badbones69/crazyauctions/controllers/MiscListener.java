package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.api.guis.types.transactions.BidMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MiscListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getInventory().getHolder(false) instanceof BidMenu) HolderManager.removeBidding(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        HolderManager.removeBidding(player);
        HolderManager.removeId(player);
        HolderManager.removeShopCategory(player);
        HolderManager.removeShopType(player);
        HolderManager.removePage(player);
    }
}