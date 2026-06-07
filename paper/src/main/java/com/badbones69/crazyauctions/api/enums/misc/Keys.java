package com.badbones69.crazyauctions.api.enums.misc;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public enum Keys {

    // contains the store id
    auction_store_id("auction_store_id", PersistentDataType.STRING),

    // contains the auction id reference for the yml file
    auction_id("auction_id", PersistentDataType.STRING),

    // the player selling or putting the item up to bid.
    auction_seller_name("auction_seller_name", PersistentDataType.STRING),

    // this contains, the number for the item which can be found in the data.yml
    auction_number("auction_number", PersistentDataType.STRING),

    // if an item is biddable
    auction_biddable("auction_biddable", PersistentDataType.BOOLEAN),

    // any button in the gui
    auction_button("auction_button", PersistentDataType.STRING),

    // the uuid of the auction.
    auction_uuid("auction_uuid", PersistentDataType.STRING),

    // contains the auction item price
    auction_price("auction_price", PersistentDataType.INTEGER);

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final String NamespacedKey;
    private final PersistentDataType type;

    Keys(@NotNull final String NamespacedKey, @NotNull final PersistentDataType type) {
        this.NamespacedKey = NamespacedKey;
        this.type = type;
    }

    public @NotNull final NamespacedKey getNamespacedKey() {
        return new NamespacedKey(this.plugin, this.plugin.getName().toLowerCase() + "_" + this.NamespacedKey);
    }

    public @NotNull final PersistentDataType getType() {
        return this.type;
    }
}