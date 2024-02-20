package com.ryderbelserion.crazyauctions.bootstrap;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.ryderbelserion.crazyauctions.CrazyAuctions;
import com.ryderbelserion.crazyauctions.api.PaperAbstractPlugin;

public class CrazyLoader implements PluginBootstrap {

    private PaperAbstractPlugin plugin;

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        this.plugin = new PaperAbstractPlugin(context.getDataDirectory().toFile());
        this.plugin.init();
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new CrazyAuctions(this.plugin);
    }
}