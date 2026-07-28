package com.badbones69.crazyauctions.api.builders.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NonNull;
import java.util.function.Consumer;

public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {

    public ItemBuilder(@NonNull final ItemType itemType, final int amount, @NonNull final Consumer<BaseItemBuilder> consumer) {
        super(itemType, amount, consumer);
    }

    public ItemBuilder(@NonNull final ItemType itemType, @NonNull final Consumer<BaseItemBuilder> consumer) {
        super(itemType, consumer);
    }

    public ItemBuilder(@NonNull final ItemType itemType, final int amount) {
        super(itemType, amount, consumer -> {});
    }

    public ItemBuilder(@NonNull final ItemType itemType) {
        this(itemType, 1);
    }

    public ItemBuilder(@NonNull final ItemStack itemStack) {
        super(itemStack);
    }

    public ItemBuilder(@NonNull final String itemStack) {
        super(itemStack);
    }

    public static ItemBuilder from(@NonNull final ItemType itemType, final int amount, @NonNull final Consumer<BaseItemBuilder> consumer) {
        return new ItemBuilder(itemType, amount, consumer);
    }

    public static ItemBuilder from(@NonNull final ItemType itemType, @NonNull final Consumer<BaseItemBuilder> consumer) {
        return from(itemType, 1, consumer);
    }

    public static ItemBuilder from(@NonNull final ItemType itemType, final int amount) {
        return from(itemType, amount, consumer -> {});
    }

    public static ItemBuilder from(@NonNull final ItemType itemType) {
        return from(itemType, 1, _ -> {});
    }

    public static ItemBuilder from(@NonNull final String itemType, final int amount) {
        return new ItemBuilder(itemType).setAmount(amount);
    }

    public static ItemBuilder from(@NonNull final String itemType) {
        return from(itemType, 1);
    }

    public static ItemBuilder from(@NonNull final ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }
}