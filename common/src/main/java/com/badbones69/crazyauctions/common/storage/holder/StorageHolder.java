package com.badbones69.crazyauctions.common.storage.holder;

import com.badbones69.crazyauctions.common.storage.impl.ConnectionFactory;
import org.jspecify.annotations.NullMarked;
import us.crazycrew.api.enums.ShopType;
import java.util.UUID;

@NullMarked
public final class StorageHolder {

    private final ConnectionFactory factory;

    public StorageHolder(final ConnectionFactory factory) {
        this.factory = factory;
    }

    public StorageHolder init() {
        this.factory.init();

        return this;
    }

    public StorageHolder reload() {
        this.factory.reload();

        return this;
    }

    public StorageHolder save() {
        this.factory.save();

        return this;
    }

    public void addItem(final UUID uuid, final String name, final String base64, final long price, final ShopType shopType) {
        this.factory.addItem(uuid, name, base64, price, shopType);
    }

    public boolean hasExpiredItem(final UUID uuid) {
        return this.factory.hasExpiredItem(uuid);
    }
}