package com.badbones69.crazyauctions.api.enums;

import org.bukkit.inventory.ItemStack;

public class CurrencyAuctionSession {
    private final ItemStack item;
    private final int amount;
    private final double price;
    private final boolean isBid;

    public CurrencyAuctionSession(ItemStack item, int amount, double price, boolean isBid) {
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.isBid = isBid;
    }

    public ItemStack getItem() { return item; }
    public int getAmount() { return amount; }
    public double getPrice() { return price; }
    public boolean isBid() { return isBid; }
}
