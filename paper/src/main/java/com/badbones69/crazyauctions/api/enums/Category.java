package com.badbones69.crazyauctions.api.enums;

import org.jspecify.annotations.NonNull;

public enum Category {
    
    NONE("None"),
    OTHER("Other"),
    ARMOR("Armor"),
    WEAPONS("Weapons"),
    TOOLS("Tools"),
    FOOD("Food"),
    POTIONS("Potions"),
    BLOCKS("Blocks");

    private final String name;

    Category(@NonNull final String name) {
        this.name = name;
    }

    public static Category getFromName(@NonNull final String name) {
        Category category = Category.NONE;

        for (final Category value : Category.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                category = value;

                break;
            }
        }

        return category;
    }

    public @NonNull final String getName() {
        return this.name;
    }
}