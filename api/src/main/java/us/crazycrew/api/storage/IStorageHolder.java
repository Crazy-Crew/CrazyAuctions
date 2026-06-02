package us.crazycrew.api.storage;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.enums.ShopType;
import java.util.UUID;

public abstract class IStorageHolder {

    public abstract @NonNull IStorageHolder init();

    public abstract void reload();

    public abstract void insertUser(@NonNull final UUID uuid, @NonNull final String name);

    public abstract void addItem(
            @NonNull final UUID uuid,
            @NonNull final String name,
            @NonNull final String base64,
            final long price,
            @NonNull final ShopType shopType
    );

    public boolean hasExpiredItem(@NonNull final UUID uuid) { // only used for yaml storage.
        return false;
    }

    public boolean hasItem(@NonNull final UUID uuid) { // only used for yaml storage.
        return false;
    }

    public boolean hasUser(@NonNull final UUID uuid) { // only used for sql storage.
        return false;
    }

    public abstract void stop();

}