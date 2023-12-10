package us.crazycrew.crazyauctions;

import com.badbones69.crazyauctions.common.AuctionsFactory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrazyAuctions extends JavaPlugin {

    private final AuctionsFactory factory;

    public CrazyAuctions(AuctionsFactory factory) {
        this.factory = factory;
    }

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public AuctionsFactory getFactory() {
        return this.factory;
    }
}