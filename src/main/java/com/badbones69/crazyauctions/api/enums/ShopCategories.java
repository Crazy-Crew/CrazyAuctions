package com.badbones69.crazyauctions.api.enums;

import com.badbones69.crazyauctions.api.economy.Currency;
import com.badbones69.crazyauctions.utils.ItemBuilder;

import java.util.HashMap;

public enum ShopCategories {
    
    SELL("Sell"),
    BID("Bid");
    
    private final String name;

    private final HashMap<ShopCategories, Options> shopCategories = new HashMap<>();
    
    /**
     * @param name name of the Shop Type.
     */
    ShopCategories(String name) {
        this.name = name;
    }
    
    /**
     * @param name name of the Type you want.
     * @return Returns the Type as an Enum.
     */
    public static ShopCategories getFromName(String name) {
        for (ShopCategories type : ShopCategories.values()) {
            if (type.getName().equalsIgnoreCase(name)) return type;
        }

        return null;
    }

    public Currency getCurrency() {
        return shopCategories.get(this).currency;
    }

    public int getCost() {
        return shopCategories.get(this).cost;
    }
    
    /**
     * @return Returns the type name as a string.
     */
    public String getName() {
        return name;
    }

    private static class Options {

        private final ItemBuilder itemBuilder;

        private final int slot;
        private final boolean inMenu;
        private int cost;
        private final Currency currency;

        public Options(ItemBuilder itemBuilder, int slot, boolean inMenu, int cost, Currency currency) {
            this.itemBuilder = itemBuilder;
            this.slot = slot;
            this.inMenu = inMenu;
            this.cost = cost;
            this.currency = currency;
        }

        public ItemBuilder getItemBuilder() {
            return itemBuilder;
        }

        public int getSlot() {
            return slot;
        }

        public int getCost() {
            return cost;
        }

        public Currency getCurrency() {
            return currency;
        }

        public boolean isInMenu() {
            return inMenu;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }
    }
}