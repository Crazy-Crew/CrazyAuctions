package com.badbones69.crazyauctions.api.builders.types;

import com.badbones69.crazyauctions.api.FileManager.Files;
import com.badbones69.crazyauctions.api.builders.InventoryBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AuctionsMenu extends InventoryBuilder {

    public AuctionsMenu(Player player, String title, int size) {
        super(player, title, size);
    }

    @Override
    public InventoryBuilder build() {
        FileConfiguration config = Files.CONFIG.getFile();

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {

    }
}