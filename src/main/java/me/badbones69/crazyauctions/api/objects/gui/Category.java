package me.badbones69.crazyauctions.api.objects.gui;

import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Category {
    
    private String name;
    private int slot;
    private ItemBuilder displayItem;
    private List<ItemBuilder> items = new ArrayList<>();
    
    public Category(String name) {
        this.name = name;
        String filePath = "Settings.Categories." + name + ".";
        FileConfiguration config = Files.CONFIG.getFile();
        slot = config.getInt(filePath + "Display.Slot");
        displayItem = new ItemBuilder()
        .setMaterial(config.getString(filePath + "Display.Item"))
        .setName(config.getString(filePath + "Display.Name"))
        .setLore(config.getStringList(filePath + "Display.Lore"));
        config.getStringList(filePath + "Items").forEach(item -> items.add(new ItemBuilder().setMaterial(item)));
    }
    
    public String getName() {
        return name;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public ItemBuilder getDisplayItem() {
        return displayItem;
    }
    
    public List<ItemBuilder> getItems() {
        return items;
    }
    
}