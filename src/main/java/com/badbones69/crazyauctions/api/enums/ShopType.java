package com.badbones69.crazyauctions.api.enums;

public enum ShopType {
    
    SELL("Sell"), BID("Bid");
    
    private final String name;
    
    /**
     * @param name name of the Shop Type.
     */
    ShopType(String name) {
        this.name = name;
    }
    
    /**
     * @param name name of the Type you want.
     * @return Returns the Type as an Enum.
     */
    public static ShopType getFromName(String name) {
        for (ShopType type : ShopType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
    
    /**
     * @return Returns the type name as a string.
     */
    public String getName() {
        return name;
    }
}