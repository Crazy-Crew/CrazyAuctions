package com.badbones69.crazyauctions.common.enums;

import org.jspecify.annotations.NonNull;

public enum MigrationKeys {

    legacy_messages("legacy_messages", "<red>Converts messages in messages.yml from legacy color codes to MiniMessage!"),
    legacy_config("legacy_config", "<red>Converts config options in config.yml from legacy color codes to MiniMessage!"),
    none("none", "<red>This does nothing.");

    private final String desc;
    private final String name;

    MigrationKeys(@NonNull final String name, @NonNull final String desc) {
        this.desc = desc;
        this.name = name;
    }

    public static MigrationKeys fromName(final String name) {
        MigrationKeys type = MigrationKeys.none;

        for (MigrationKeys key : MigrationKeys.values()) {
            if (key.getName().equalsIgnoreCase(name)) {
                type = key;

                break;
            }
        }

        return type;
    }

    public @NonNull final String getName() {
        return this.name;
    }

    public @NonNull final String getDesc() {
        return this.desc;
    }
}