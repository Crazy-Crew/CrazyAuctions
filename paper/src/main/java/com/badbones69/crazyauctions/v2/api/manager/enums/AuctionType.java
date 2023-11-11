package com.badbones69.crazyauctions.api.manager.enums;

/**
 * Description: Defines the auction type to use
 */
public enum AuctionType {

    BID("Bid"),
    SELL("Sell");

    private final String name;

    AuctionType(String name) {
        this.name = name;
    }

    public static AuctionType getTypeFromName(String name) {
        for (AuctionType type : AuctionType.values()) {
            if (type.getName().equalsIgnoreCase(name)) return type;
        }

        return null;
    }

    public String getName() {
        return this.name;
    }
}