package com.badbones69.crazyauctions.common.api;

import com.badbones69.crazyauctions.common.api.interfaces.AbstractPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class CrazyAuctionsPlugin {

    private static AbstractPlugin plugin = null;

    @NotNull
    public static AbstractPlugin get() {
        AbstractPlugin instance = CrazyAuctionsPlugin.plugin;

        if (instance == null) {
            throw new RuntimeException("Plugin variable not available");
        }

        return plugin;
    }

    @ApiStatus.Internal
    private CrazyAuctionsPlugin() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @ApiStatus.Internal
    public static void init(AbstractPlugin plugin) {
        if (CrazyAuctionsPlugin.plugin != null) return;

        CrazyAuctionsPlugin.plugin = plugin;
    }

    @ApiStatus.Internal
    public static void stop() {
        if (CrazyAuctionsPlugin.plugin == null) return;

        CrazyAuctionsPlugin.plugin = null;
    }
}