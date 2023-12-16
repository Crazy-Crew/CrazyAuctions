package us.crazycrew.crazyauctions.bootstrap;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.CrazyAuctions;
import us.crazycrew.crazyauctions.api.PaperAbstractPlugin;

public class CrazyLoader implements PluginBootstrap {

    private PaperAbstractPlugin plugin;

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        this.plugin = new PaperAbstractPlugin(context.getDataDirectory().toFile());
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new CrazyAuctions(this.plugin);
    }
}