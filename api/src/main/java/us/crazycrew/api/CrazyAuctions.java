package us.crazycrew.api;

import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.items.IAuctionItem;
import us.crazycrew.api.registry.IPlayerRegistry;
import us.crazycrew.api.storage.IStorageHolder;
import java.nio.file.Path;
import java.util.UUID;

public abstract class CrazyAuctions<S, K, I> {

    public static final UUID CONSOLE_UUID = new UUID(0, 0);
    public static final String CONSOLE_NAME = "Console";

    public static final String namespace = "crazyauctions";

    public abstract @NonNull IAuctionItem<I> getItem(@NonNull final I itemStack);

    public abstract @NonNull MessageRegistry getMessageRegistry();

    public abstract @NonNull IStorageHolder getStorageHolder();

    public abstract @NonNull IPlayerRegistry getUserRegistry();

    public abstract @NonNull Path getDataPath();

    public abstract @NonNull K getFusion();

    public abstract void loadExamples();

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