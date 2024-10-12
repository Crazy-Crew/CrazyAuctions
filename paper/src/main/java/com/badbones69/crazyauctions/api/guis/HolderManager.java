package com.badbones69.crazyauctions.api.guis;

import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.ShopType;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HolderManager {

    private static final Map<UUID, Integer> bidding = new HashMap<>();
    private static final Map<UUID, ShopType> shopTypes = new HashMap<>();
    private static final Map<UUID, Category> shopCategory = new HashMap<>();

    public static void addShopCategory(final Player player, final Category category) {
        shopCategory.put(player.getUniqueId(), category);
    }

    public static void removeShopCategory(final Player player) {
        shopCategory.remove(player.getUniqueId());
    }

    public static Category getShopCategory(final Player player) {
        return shopCategory.get(player.getUniqueId());
    }

    public static Map<UUID, Category> getShopCategory() {
        return shopCategory;
    }

    public static void addBidding(final Player player, final int amount) {
        bidding.put(player.getUniqueId(), amount);
    }

    public static void removeBidding(final Player player) {
        bidding.remove(player.getUniqueId());
    }

    public static int getBidding(final Player player) {
        return bidding.get(player.getUniqueId());
    }

    public static Map<UUID, Integer> getBidding() {
        return bidding;
    }

    public static boolean containsBidding(final Player player) {
        return bidding.containsKey(player.getUniqueId());
    }

    public static void addShopType(final Player player, final ShopType shopType) {
        shopTypes.put(player.getUniqueId(), shopType);
    }

    public static void removeShopType(final Player player) {
        shopTypes.remove(player.getUniqueId());
    }

    public static ShopType getShopType(final Player player) {
        return shopTypes.get(player.getUniqueId());
    }

    public static Map<UUID, ShopType> getShopType() {
        return shopTypes;
    }
}