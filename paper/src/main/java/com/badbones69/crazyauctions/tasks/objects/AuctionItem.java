package com.badbones69.crazyauctions.tasks.objects;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SuppressWarnings("UnusedAssignment")
public class AuctionItem {

    private final UUID uuid;
    private final String name;

    private final String id;

    private final String store_id;
    private final ItemStack itemStack;
    private long price;
    private final long time_till_expire;
    private final long full_expire;

    private String bidder_uuid;
    private String bidder_name;
    private long topBid;

    private final boolean isBiddable;

    public AuctionItem(final String uuid, final String name, final String id, final String item, final String store_id, final long price, final long time_till_expire, final long full_expire, final String bidder_uuid, final String bidder_name, final long topBid, final boolean biddable) {
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.id = id;

        this.itemStack = Methods.fromBase64(item);

        this.store_id = store_id;
        this.price = price;

        this.time_till_expire = time_till_expire;
        this.full_expire = full_expire;

        this.bidder_uuid = bidder_uuid;
        this.bidder_name = bidder_name;
        this.topBid = topBid;

        this.isBiddable = biddable;
    }

    public final UUID getUuid() {
        return this.uuid;
    }

    public final String getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    public final String getStoreID() {
        return this.store_id;
    }

    public final long getPrice() {
        return this.price;
    }

    public void setPrice(final long price) {
        this.price = price;
    }

    public final long getTimeTillExpire() {
        return this.time_till_expire;
    }

    public final long getFullExpire() {
        return this.full_expire;
    }

    public final String getBidderUUID() {
        return this.bidder_uuid;
    }

    public void setBidderUUID(final String uuid) {
        this.bidder_uuid = uuid;
    }

    public final String getBidderName() {
        return this.bidder_name;
    }

    public void setBidderName(String name) {
        this.bidder_name = name;
    }

    public final long getTopBid() {
        return this.topBid;
    }

    public void setTopBid(long topBid) {
        if (topBid <= 0L) topBid = this.topBid;

        this.topBid = topBid;
    }

    public final boolean isBiddable() {
        return this.isBiddable;
    }

    public final ItemStack asItemStack() {
        return this.itemStack;
    }

    public final ItemBuilder asItemBuilder() {
        return new ItemBuilder(this.itemStack);
    }

    public final String asBase64() {
        return Methods.toBase64(asItemStack());
    }

    public final ItemBuilder getActiveItem(final ShopType shopType) {
        final FileConfiguration configuration = Files.config.getConfiguration();

        final ItemBuilder itemBuilder = ItemBuilder.convertItemStack(this.itemStack.clone());

        final String time = Methods.convertToTime(getTimeTillExpire());

        final List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

        lore.add(" ");

        if (shopType == ShopType.BID && isBiddable()) {
            final String priceFormat = String.format(Locale.ENGLISH, "%,d", this.topBid);

            for (final String line : configuration.getStringList("Settings.GUISettings.Bidding")) {
                String newLine = line.replace("%TopBid%", priceFormat)
                        .replace("%topbid%", priceFormat);

                newLine = line.replace("%Seller%", getName())
                        .replace("%seller%", getName());

                newLine = line.replace("%TopBidder%", getBidderName())
                        .replace("%topbidder%", getBidderName());

                lore.add(newLine.replace("%Time%", time)
                        .replace("%time%", time));
            }
        } else {
            final String priceFormat = String.format(Locale.ENGLISH, "%,d", this.price);

            for (final String line : configuration.getStringList("Settings.GUISettings.SellingItemLore")) {
                String newLine = line.replace("%TopBid%", priceFormat)
                        .replace("%topbid%", priceFormat);

                newLine = line.replace("%Seller%", getName())
                        .replace("%seller%", getName());

                lore.add(newLine.replace("%Time%", time)
                        .replace("%time%", time)
                        .replace("%price%", priceFormat).replace("%Price%", priceFormat));
            }
        }

        itemBuilder.setLore(lore)
                .addString(this.id, Keys.auction_number.getNamespacedKey())
                .addString(getStoreID(), Keys.auction_store_id.getNamespacedKey())
                .addString(this.uuid.toString(), Keys.auction_uuid.getNamespacedKey());

        return itemBuilder;
    }

    public final ItemBuilder getCurrentItem() {
        final FileConfiguration configuration = Files.config.getConfiguration();

        final ItemBuilder itemBuilder = ItemBuilder.convertItemStack(this.itemStack.clone());

        final String priceFormat = String.format(Locale.ENGLISH, "%,d", this.price);

        final String time = Methods.convertToTime(getTimeTillExpire());

        final List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

        lore.add(" ");

        for (final String line : configuration.getStringList("Settings.GUISettings.CurrentLore")) {
            lore.add(line.replace("%Time%", time).replace("%time%", time)
                    .replace("%price%", priceFormat)
                    .replace("%Price%", priceFormat));
        }

        itemBuilder.setLore(lore).addString(getStoreID(), Keys.auction_store_id.getNamespacedKey());;

        return itemBuilder;
    }
}