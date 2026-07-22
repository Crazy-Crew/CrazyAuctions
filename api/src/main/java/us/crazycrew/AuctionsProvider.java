package us.crazycrew;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.CrazyAuctions;

public class AuctionsProvider {

    private static CrazyAuctions instance;

    @ApiStatus.Internal
    private AuctionsProvider() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static CrazyAuctions api() {
        return instance;
    }

    @ApiStatus.Internal
    public static void register(@NonNull final CrazyAuctions instance) {
        AuctionsProvider.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        AuctionsProvider.instance = null;
    }
}