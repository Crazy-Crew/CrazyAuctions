package com.badbones69.crazyauctions.api.builders.items.plugins.types.custom;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.badbones69.crazyauctions.api.builders.items.BaseItemBuilder;
import com.badbones69.crazyauctions.api.builders.items.plugins.ICustomItem;
import com.badbones69.crazyauctions.api.builders.items.plugins.types.VanillaItemStack;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import java.util.Optional;

public class NexoCustomItem extends ICustomItem {

    public NexoCustomItem(@NonNull final BaseItemBuilder builder, @NonNull final String item, final boolean isEnabled) {
        super(builder, item, isEnabled);
    }

    private ItemStack itemStack;

    @Override
    public @NonNull final Optional<ItemStack> getItemStack() {
        return Optional.ofNullable(this.itemStack);
    }

    @Override
    public @NonNull final NexoCustomItem init() {
        final String impl = getImpl();

        if (!this.isEnabled && !this.fusion.isPluginEnabled(impl)) {
            new VanillaItemStack(this.builder, this.item).init();

            return this;
        }

        if (!NexoItems.exists(this.item)) {
            this.fusion.log(Level.WARNING, "The id %s does not exist as a %s item! Attempting falling back to vanilla item!", this.item, impl);

            new VanillaItemStack(this.builder, this.item).init();

            return this;
        }

        final ItemBuilder builder = NexoItems.itemFromId(this.item);

        if (builder == null) {
            throw new FusionException("The id " + this.item + " is not a valid %s item!".formatted(impl));
        }

        this.itemStack = builder.build();

        return this;
    }

    @Override
    public @NonNull final String getImpl() {
        return "Nexo";
    }
}