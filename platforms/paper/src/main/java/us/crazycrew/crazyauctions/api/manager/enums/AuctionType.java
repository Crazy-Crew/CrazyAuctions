package us.crazycrew.crazyauctions.api.manager.enums;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Created: 2/18/2023
 * Time: Unknown
 * Last Edited: 2/28/2023 @ 3:04 AM
 *
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