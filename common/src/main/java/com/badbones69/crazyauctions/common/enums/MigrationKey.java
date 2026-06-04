package com.badbones69.crazyauctions.common.enums;

import org.jspecify.annotations.NonNull;

public enum MigrationKey {

    legacy_messages("legacy_messages", "<red>Converts messages in messages.yml from legacy color codes to MiniMessage!"),
    legacy_config("legacy_config", "<red>Converts config options in config.yml from legacy color codes to MiniMessage!"),
    none("none", "<red>This does nothing.");

    private final String desc;
    private final String name;

    MigrationKey(@NonNull final String name, @NonNull final String desc) {
        this.desc = desc;
        this.name = name;
    }

    public static MigrationKey fromName(final String name) {
        MigrationKey type = MigrationKey.none;

        for (MigrationKey key : MigrationKey.values()) {
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