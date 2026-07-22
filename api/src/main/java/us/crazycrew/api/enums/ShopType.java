package us.crazycrew.api.enums;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum ShopType {
    
    SELL("Sell"),
    BID("Bid");
    
    private final String name;
    
    /**
     * @param name name of the Shop Type.
     */
    ShopType(final String name) {
        this.name = name;
    }
    
    /**
     * @param name name of the Type you want.
     * @return Returns the Type as an Enum.
     */
    public static ShopType getFromName(final String name) {
        for (final ShopType type : ShopType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return ShopType.BID;
    }
    
    /**
     * @return Returns the type name as a string.
     */
    public final String getName() {
        return this.name;
    }
}