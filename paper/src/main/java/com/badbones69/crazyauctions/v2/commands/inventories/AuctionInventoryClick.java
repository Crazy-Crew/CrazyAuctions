package com.badbones69.crazyauctions.commands.inventories;

import com.badbones69.crazyauctions.api.frame.items.ItemBuilder;
import com.badbones69.crazyauctions.frame.utils.AdventureUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AuctionInventoryClick implements Listener {

    @EventHandler
    public void onAuctionClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || (!(inventory.getHolder() instanceof AuctionInventory auctionInventory))) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();

        if (clicked != null) {
            event.getWhoClicked().sendMessage("Beep Boo Boo Beep!");

            ItemBuilder builder = ItemBuilder.setStack(new ItemStack(Material.DIAMOND_SWORD));

            builder.setAmount(3);
            builder.setDisplayName(AdventureUtils.parse("<red>This is a test</red>"));

            auctionInventory.getInventory().addItem(builder.build());
        }
    }
}