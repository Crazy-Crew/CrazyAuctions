package com.badbones69.crazyauctions.api.manager.objects.auctiontype;

import com.badbones69.crazyauctions.api.manager.enums.AuctionType;
import com.badbones69.crazyauctions.api.manager.interfaces.AuctionItem;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Description: Creates the selling auction type
 */
public class SellingAuction implements AuctionItem {

    AuctionType auctionType = AuctionType.SELL;
    UUID seller;
    long price;
    long expireTime;
    ItemStack sellingItem;

    public SellingAuction(UUID seller, long price, long expireTime, ItemStack sellingItem) {
        this.seller = seller;
        this.price = price;
        this.expireTime = expireTime;
        this.sellingItem = sellingItem;
    }

    @Override
    public AuctionType getAuctionType() {
        return auctionType;
    }

    @Override
    public UUID getSeller() {
        return seller;
    }

    @Override
    public long getPrice() {
        return price;
    }

    @Override
    public long getExpireTime() {
        return expireTime;
    }

    @Override
    public ItemStack getSellingItem() {
        return sellingItem;
    }
}