package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.api.guis.types.transactions.BidMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MiscListener implements Listener {

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getInventory().getHolder(false) instanceof BidMenu) HolderManager.removeBidding(player);
    }
}