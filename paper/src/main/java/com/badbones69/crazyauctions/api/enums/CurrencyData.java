package com.badbones69.crazyauctions.api.enums;

import org.bukkit.inventory.ItemStack;

public class CurrencyData {
    private final String id;
    private final String name;
    private final String symbol;
    private final ItemStack icon;

    public CurrencyData(String id, String name, String symbol, ItemStack icon) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public ItemStack getIcon() {
        return icon.clone();
    }
}
