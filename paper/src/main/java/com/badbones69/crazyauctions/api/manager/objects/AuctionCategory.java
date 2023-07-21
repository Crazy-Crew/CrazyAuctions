package com.badbones69.crazyauctions.api.manager.objects;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.frame.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

/**
 * Description: Creates the auction categories
 */
public class AuctionCategory {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private String name;
    private int slot;
    //private ItemBuilder displayItem;
    private List<Material> categoryItemList;

    //TODO make it so each AH can have their own categories and the default ones should be able to be disabled.
    public AuctionCategory(String name, int slot, ItemBuilder displayItem, List<Material> categoryItemList) {
        this.name = name;
        this.slot = slot;
        //this.displayItem = displayItem;
        this.categoryItemList = categoryItemList;
    }

    public String getName() {
        return name;
    }

    public int getSlot() {
        return slot;
    }

    //public ItemBuilder getDisplayItem() {
    //    return displayItem;
    //}

    public List<Material> getCategoryItemList() {
        return categoryItemList;
    }
}