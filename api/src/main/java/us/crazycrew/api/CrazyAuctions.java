package us.crazycrew.api;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.storage.IStorageHolder;
import java.nio.file.Path;

public abstract class CrazyAuctions<S, K> {

    public abstract @NonNull IStorageHolder getStorageHolder();

    public abstract @NonNull Path getDataPath();

    public abstract @NonNull K getFusion();

    public abstract void post();

    public abstract void init();

    public abstract void stop();

    public abstract void reload();

    public abstract boolean isSellModuleEnabled();

    public abstract boolean isBidModuleEnabled();

    public static class Provider {
        private static CrazyAuctions instance;

        @ApiStatus.Internal
        private Provider() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }

        public static CrazyAuctions getInstance() {
            return instance;
        }

        @ApiStatus.Internal
        public static void register(@NonNull final CrazyAuctions instance) {
            Provider.instance = instance;
        }

        @ApiStatus.Internal
        public static void unregister() {
            Provider.instance = null;
        }
    }
}