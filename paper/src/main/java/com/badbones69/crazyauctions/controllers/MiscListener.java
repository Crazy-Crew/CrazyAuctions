package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.guis.HolderManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MiscListener implements Listener {

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        FileConfiguration config = Files.config.getConfiguration();

        Player player = (Player) event.getPlayer();

        if (event.getView().getTitle().contains(Methods.strip(config.getString("Settings.Bidding-On-Item")))) HolderManager.removeBidding(player);
    }
}