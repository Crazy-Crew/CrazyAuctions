package com.ryderbelserion.crazyauctions;

public final class CrazyProvider {

    private static CrazyAuctions instance;

    private CrazyProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static CrazyAuctions get() {
        if (instance == null) {
            throw new IllegalStateException("CrazyAuctions is not loaded.");
        }

        return instance;
    }

    static void register(final CrazyAuctions instance) {
        if (get() != null) {
            return;
        }

        CrazyProvider.instance = instance;
    }

    static void unregister() {
        CrazyProvider.instance = null;
    }
}