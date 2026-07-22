package com.badbones69.crazyauctions.common.storage.impl;

import org.jspecify.annotations.NullMarked;
import us.crazycrew.api.enums.ShopType;
import java.util.UUID;

@NullMarked
public abstract class ConnectionFactory {

    public abstract void addItem(final UUID uuid, final String name, final String base64, final long price, final ShopType shopType);

    public abstract boolean hasExpiredItem(final UUID uuid);

    public abstract String getImpl();

    public abstract void reload();

    public abstract void init();

    public abstract void stop();

    public abstract void save();

}