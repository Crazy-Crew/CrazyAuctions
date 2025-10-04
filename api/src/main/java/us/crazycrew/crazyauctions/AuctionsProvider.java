package us.crazycrew.crazyauctions;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.exceptions.AuctionsException;
import us.crazycrew.crazyauctions.objects.ICrazyAuctions;

/**
 * A class used to initialize the api, so other plugins can use it.
 *
 * @author Ryder Belserion
 * @version 0.1.0
 * @since 0.1.0
 */
public class AuctionsProvider {

    @ApiStatus.Internal
    private AuctionsProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    private static ICrazyAuctions api = null;

    /**
     * Gets the instance of CrazyAuctions which lets you interact with our plugin.
     *
     * @return {@link ICrazyAuctions}
     * @since 0.1.0
     */
    public static ICrazyAuctions getApi() {
        if (api == null) {
            throw new AuctionsException("CrazyAuctions API is not loaded.");
        }

        return api;
    }

    /**
     * Creates {@link ICrazyAuctions} instance.
     *
     * @param api the {@link ICrazyAuctions} instance
     * @since 0.4
     */
    @ApiStatus.Internal
    public static void register(@NotNull final ICrazyAuctions api) {
        final ICrazyAuctions auctions = AuctionsProvider.api;

        if (auctions != null) {
            auctions.getLogger().warn("CrazyAuctions API is already initialized.");

            return;
        }

        AuctionsProvider.api = api;
    }

    /**
     * Unregisters {@link ICrazyAuctions} instance.
     *
     * @since 0.4
     */
    @ApiStatus.Internal
    public static void unregister() {
        AuctionsProvider.api = null;
    }
}