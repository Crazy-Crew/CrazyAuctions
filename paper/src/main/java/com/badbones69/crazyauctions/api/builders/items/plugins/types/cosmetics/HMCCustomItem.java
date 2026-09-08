package com.badbones69.crazyauctions.api.builders.items.plugins.types.cosmetics;

import com.badbones69.crazyauctions.api.builders.items.BaseItemBuilder;
import com.badbones69.crazyauctions.api.builders.items.plugins.ICustomItem;
import com.badbones69.crazyauctions.api.builders.items.plugins.types.VanillaItemStack;
import com.ryderbelserion.fusion.core.api.enums.Level;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.PhysicalWrap;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.WrapsLoader;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import java.util.Map;
import java.util.Optional;

public class HMCCustomItem extends ICustomItem {

    public HMCCustomItem(@NonNull final BaseItemBuilder builder, @NonNull final String item, final boolean isEnabled) {
        super(builder, item, isEnabled);
    }

    private ItemStack itemStack;

    @Override
    public @NonNull final Optional<ItemStack> getItemStack() {
        return Optional.ofNullable(this.itemStack);
    }

    @Override
    public @NonNull final HMCCustomItem init() {
        final String impl = getImpl();

        if (!this.isEnabled && !this.fusion.isPluginEnabled(impl)) {
            new VanillaItemStack(this.builder, this.item).init();

            return this;
        }

        final HMCWraps plugin = (HMCWraps) this.pluginManager.getPlugin(impl);

        if (plugin == null) {
            new VanillaItemStack(this.builder, this.item).init();

            return this;
        }

        final WrapsLoader loader = plugin.getWrapsLoader();

        final Map<String, Wrap> wraps = loader.getWraps();

        if (!wraps.containsKey(this.item)) {
            this.fusion.log(Level.WARNING, "The id %s does not exist as a %s item! Attempting falling back to vanilla item!", this.item, impl);

            new VanillaItemStack(this.builder, this.item).init();

            return this;
        }

        final Wrap wrap = wraps.get(this.item);

        final PhysicalWrap physical = wrap.getPhysical();

        if (physical == null) {
            this.fusion.log(Level.ERROR, "There is no physical wrap associated with %s using %s", this.item, impl);

            return this;
        }

        this.itemStack = physical.toItem(plugin, null);

        return this;
    }

    @Override
    public @NonNull final String getImpl() {
        return "HMCWraps";
    }
}