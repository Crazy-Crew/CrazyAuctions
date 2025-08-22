package com.badbones69.crazyauctions.commands.features.admin.migrate.enums;

import org.jetbrains.annotations.NotNull;

public enum MigrationType {

    AUCTIONS_DEPRECATED("AuctionsDeprecated");

    private final String name;

    MigrationType(@NotNull final String name) {
        this.name = name;
    }

    public @NotNull final String getName() {
        return this.name;
    }

    public static MigrationType fromName(@NotNull final String name) {
        MigrationType type = null;

        for (final MigrationType key : MigrationType.values()) {
            if (key.getName().equalsIgnoreCase(name)) {
                type = key;

                break;
            }
        }

        return type;
    }
}