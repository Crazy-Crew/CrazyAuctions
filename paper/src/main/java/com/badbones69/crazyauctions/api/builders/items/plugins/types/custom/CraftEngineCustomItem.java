package com.badbones69.crazyauctions.api.builders.items.plugins.types.custom;

import com.ryderbelserion.fusion.core.api.enums.Level;
import com.badbones69.crazyauctions.api.builders.items.BaseItemBuilder;
import com.badbones69.crazyauctions.api.builders.items.plugins.ICustomItem;
import com.badbones69.crazyauctions.api.builders.items.plugins.types.VanillaItemStack;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import java.util.Optional;

public class CraftEngineCustomItem extends ICustomItem {

    public CraftEngineCustomItem(@NonNull final BaseItemBuilder builder, @NonNull final String item, final boolean isEnabled) {
        super(builder, item, isEnabled);
    }

    private ItemStack itemStack;

    @Override
    public @NonNull final Optional<ItemStack> getItemStack() {
        return Optional.ofNullable(this.itemStack);
    }

    @Override
    public @NonNull final CraftEngineCustomItem init() {
        final String impl = getImpl();

        if (!this.isEnabled && !this.fusion.isPluginEnabled(impl)) {
            new VanillaItemStack(this.builder, this.item).init();

            return this;
        }

        Optional.ofNullable(CraftEngineItems.byId(this.item)).ifPresentOrElse(item -> this.itemStack = item.buildBukkitItem(), () -> {
            this.fusion.log(Level.WARNING, "The id %s does not exist as a %s item! Attempting falling back to vanilla item!", this.item, impl);

            new VanillaItemStack(this.builder, this.item).init();
        });

        return this;
    }

    @Override
    public @NonNull final String getImpl() {
        return "CraftEngine";
    }
}