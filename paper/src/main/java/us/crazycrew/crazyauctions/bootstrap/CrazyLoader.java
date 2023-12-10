package us.crazycrew.crazyauctions.bootstrap;

import com.badbones69.crazyauctions.common.AuctionsFactory;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.CrazyAuctions;

public class CrazyLoader extends AuctionsFactory implements PluginBootstrap {

    private AuctionsFactory factory;

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        super.load(context.getDataDirectory().toFile());
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new CrazyAuctions(this);
    }
}