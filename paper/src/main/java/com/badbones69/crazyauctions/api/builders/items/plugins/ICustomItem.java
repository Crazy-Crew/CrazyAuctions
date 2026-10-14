package com.badbones69.crazyauctions.api.builders.items.plugins;

import com.badbones69.crazyauctions.api.builders.items.BaseItemBuilder;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import java.util.Optional;

public abstract class ICustomItem {

    protected final FusionPaper fusion = (FusionPaper) FusionProvider.getInstance();

    protected final JavaPlugin plugin = this.fusion.getPlugin();

    protected final Server server = this.plugin.getServer();

    protected final PluginManager pluginManager = this.server.getPluginManager();

    protected final BaseItemBuilder builder;
    protected final boolean isEnabled;
    protected final String item;

    public ICustomItem(@NonNull final BaseItemBuilder builder, @NonNull final String item, final boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.builder = builder;
        this.item = item;
    }

    public abstract @NonNull Optional<ItemStack> getItemStack();

    public abstract @NonNull ICustomItem init();

    public abstract String getImpl();
}