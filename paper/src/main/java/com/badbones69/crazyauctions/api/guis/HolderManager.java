package com.badbones69.crazyauctions.api.guis;

import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.ShopType;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HolderManager {

    private static final Map<UUID, Integer> bidding = new HashMap<>();
    private static final Map<UUID, String> bidIds = new HashMap<>();
    private static final Map<UUID, ShopType> shopTypes = new HashMap<>();
    private static final Map<UUID, Category> shopCategory = new HashMap<>();
    private static final Map<UUID, List<Integer>> pages = new HashMap<>();
    private static final Map<UUID, String> ids = new HashMap<>();

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

    public static void addPages(final Player player, List<Integer> list) {
        pages.put(player.getUniqueId(), list);
    }

    public static void addPage(final Player player, final int page) {
        final UUID uuid = player.getUniqueId();

        final List<Integer> list = pages.get(uuid);
        list.add(page);

        pages.put(uuid, list);
    }

    public static void removePage(final Player player, final int page) {
        final UUID uuid = player.getUniqueId();

        final List<Integer> list = pages.get(uuid);

        list.remove(page);

        pages.put(uuid, list);
    }

    public static boolean containsPage(final Player player) {
        return pages.containsKey(player.getUniqueId());
    }

    public static List<Integer> getPages(final Player player) {
        return pages.get(player.getUniqueId());
    }

    public static Map<UUID, List<Integer>> getPages() {
        return pages;
    }

    public static void addBidId(final Player player, final String id) {
        bidIds.put(player.getUniqueId(), id);
    }

    public static void removeBidId(final Player player) {
        bidIds.remove(player.getUniqueId());
    }

    public static String getBidId(final Player player) {
        return bidIds.get(player.getUniqueId());
    }

    public static Map<UUID, String> getBidIds() {
        return bidIds;
    }

    public static void addId(final Player player, final String id) {
        ids.put(player.getUniqueId(), id);
    }

    public static void removeId(final Player player) {
        ids.remove(player.getUniqueId());
    }

    public static String getId(final Player player) {
        return ids.get(player.getUniqueId());
    }

    public static Map<UUID, String> getIds() {
        return ids;
    }
}