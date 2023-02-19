package com.badbones69.crazyauctions.api.auctionhouse.objects;

import com.badbones69.crazyauctions.utils.ItemUtils;
import net.dehya.ruby.items.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;

public class AuctionButtons {

    private ItemBuilder sellingItemsButton;
    private ItemBuilder sellingInfoButton;
    private ItemBuilder biddingInfoButton;
    private ItemBuilder currentListingsInfoButton;
    private ItemBuilder expiredItemsButton;
    private ItemBuilder expiredInfoButton;
    private ItemBuilder categoriesButton;
    private ItemBuilder categoriesInfoButton;
    private ItemBuilder nextPageButton;
    private ItemBuilder refreshPageButton;
    private ItemBuilder backPageButton;
    private ItemBuilder switchModeButton;

    public AuctionButtons(FileConfiguration file) {
        String path = "auction-house.settings.buttons.";
        sellingItemsButton = ItemUtils.convertString(file.getString(path + "selling-items"));
        sellingInfoButton = ItemUtils.convertString(file.getString(path + "info.selling-items"));
        biddingInfoButton = ItemUtils.convertString(file.getString(path + "info.bidding"));
        currentListingsInfoButton = ItemUtils.convertString(file.getString(path + "info.current-listings"));
        expiredItemsButton = ItemUtils.convertString(file.getString(path + "expired-items"));
        expiredInfoButton = ItemUtils.convertString(file.getString(path + "info.expired-items"));
        categoriesButton = ItemUtils.convertString(file.getString(path + "categories"));
        categoriesInfoButton = ItemUtils.convertString(file.getString(path + "info.categories"));
        nextPageButton = ItemUtils.convertString(file.getString(path + "next-page"));
        refreshPageButton = ItemUtils.convertString(file.getString(path + "refresh-page"));
        backPageButton = ItemUtils.convertString(file.getString(path + "back-page"));
        switchModeButton = ItemUtils.convertString(file.getString(path + "switch-mode"));
    }

    public ItemBuilder getSellingItemsButton() {
        return sellingItemsButton;
    }

    public ItemBuilder getSellingInfoButton() {
        return sellingInfoButton;
    }

    public ItemBuilder getBiddingInfoButton() {
        return biddingInfoButton;
    }

    public ItemBuilder getCurrentListingsInfoButton() {
        return currentListingsInfoButton;
    }

    public ItemBuilder getExpiredItemsButton() {
        return expiredItemsButton;
    }

    public ItemBuilder getExpiredInfoButton() {
        return expiredInfoButton;
    }

    public ItemBuilder getCategoriesButton() {
        return categoriesButton;
    }

    public ItemBuilder getCategoriesInfoButton() {
        return categoriesInfoButton;
    }

    public ItemBuilder getNextPageButton() {
        return nextPageButton;
    }

    public ItemBuilder getRefreshPageButton() {
        return refreshPageButton;
    }

    public ItemBuilder getBackPageButton() {
        return backPageButton;
    }

    public ItemBuilder getSwitchModeButton() {
        return switchModeButton;
    }

}