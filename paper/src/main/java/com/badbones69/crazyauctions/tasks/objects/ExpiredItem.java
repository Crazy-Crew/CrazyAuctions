package com.badbones69.crazyauctions.tasks.objects;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ExpiredItem {

    private final UUID uuid;
    private final String name;

    private final String id;

    private final String store_id;
    private final ItemStack itemStack;
    private final long full_expire;
    private final long price;

    public ExpiredItem(final String uuid, final String name, final String id, final String item, final String store_id, final long full_expire, final long price) {
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.id = id;

        this.itemStack = Methods.fromBase64(item);

        this.store_id = store_id;
        this.full_expire = full_expire;

        this.price = price;
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

    public final long getFullExpire() {
        return this.full_expire;
    }

    public final ItemStack asItemStack() {
        return this.itemStack;
    }

    public final long getPrice() {
        return this.price;
    }

    public final String asBase64() {
        return Methods.toBase64(asItemStack());
    }

    public final ItemBuilder getExpiredItem() {
        final FileConfiguration configuration = Files.config.getConfiguration();

        final ItemBuilder itemBuilder = ItemBuilder.convertItemStack(this.itemStack.clone());

        final String priceFormat = String.format(Locale.ENGLISH, "%,d", this.price);

        final String time = Methods.convertToTime(getFullExpire());

        final List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

        lore.add(" ");

        for (final String line : configuration.getStringList("Settings.GUISettings.Cancelled/ExpiredLore")) {
            lore.add(line.replace("%Time%", time).replace("%time%", time)
                    .replace("%price%", priceFormat)
                    .replace("%Price%", priceFormat));
        }

        itemBuilder.setLore(lore)
                .addString(getStoreID(), Keys.auction_store_id.getNamespacedKey());;

        return itemBuilder;
    }
}