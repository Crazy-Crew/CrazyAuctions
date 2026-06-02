package us.crazycrew.api.enums;

import org.jspecify.annotations.NonNull;

public enum ShopType {
    
    SELL("Sell"),
    BID("Bid");
    
    private final String name;
    
    /**
     * @param name name of the Shop Type.
     */
    ShopType(@NonNull final String name) {
        this.name = name;
    }
    
    /**
     * @param name name of the Type you want.
     * @return Returns the Type as an Enum.
     */
    public static @NonNull ShopType getFromName(@NonNull final String name) {
        for (ShopType type : ShopType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return ShopType.BID;
    }
    
    /**
     * @return Returns the type name as a string.
     */
    public @NonNull final String getName() {
        return this.name;
    }
}