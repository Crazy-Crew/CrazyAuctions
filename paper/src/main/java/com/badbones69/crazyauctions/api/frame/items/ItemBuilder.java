package com.badbones69.crazyauctions.api.frame.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {

    public ItemBuilder() {
        super();
    }

    public ItemBuilder(ItemStack itemStack) {
        super(itemStack);
    }

    public static ItemBuilder setStack(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public static ItemBuilder setMaterial(Material material) {
        return new ItemBuilder(new ItemStack(material));
    }
}