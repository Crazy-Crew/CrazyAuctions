package com.badbones69.crazyauctions.api.builders.items.plugins.types;

import com.badbones69.crazyauctions.api.builders.items.BaseItemBuilder;
import com.badbones69.crazyauctions.api.builders.items.plugins.ICustomItem;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NonNull;
import java.util.Optional;

public class VanillaItemStack extends ICustomItem {

    public VanillaItemStack(@NonNull final BaseItemBuilder builder, @NonNull final String item) {
        super(builder, item, true);
    }

    @Override
    public @NonNull final Optional<ItemStack> getItemStack() {
        return Optional.of(this.builder.getItemStack());
    }

    @Override
    public @NonNull final VanillaItemStack init() {
        final ItemType itemType = ItemUtils.getItemType(this.item);

        if (itemType == null) {
            this.builder.withBase64(this.item);

            return this;
        }

        this.builder.withType(itemType);

        return this;
    }

    @Override
    public @NonNull final String getImpl() {
        return "Vanilla";
    }
}