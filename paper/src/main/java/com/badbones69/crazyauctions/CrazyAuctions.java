package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.CrazyPlatform;
import com.badbones69.crazyauctions.controllers.CacheListener;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.controllers.MarcoListener;
import com.badbones69.crazyauctions.controllers.TrafficListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.badbones69.crazyauctions.datafixer.ConfigFixer;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import com.ryderbelserion.fusion.paper.builders.folia.Scheduler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class CrazyAuctions extends JavaPlugin {

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private CrazyManager crazyManager;
    private VaultSupport support;

    private CrazyPlatform platform;
    private FusionPaper fusion;

    @Override
    public void onEnable() {
        this.platform = new CrazyPlatform(this, new FusionPaper(this));
        this.platform.init();

        this.fusion = this.platform.getFusion();

        this.crazyManager = new CrazyManager();

        final Server server = getServer();
        final PluginManager pluginManager = server.getPluginManager();

        List.of(
                new GuiListener(),
                new MarcoListener(),

                new TrafficListener(),

                new CacheListener()
        ).forEach(listener -> pluginManager.registerEvents(listener, this));

        this.support = new VaultSupport();
        this.support.setupEconomy();

        new FoliaScheduler(this, Scheduler.global_scheduler) {
            @Override
            public void run() {
                Methods.updateAuction();
            }
        }.runAtFixedRate(0L, 5000L);

        new ConfigFixer().onEnable();

        new Metrics(this, 4624);
    }

    @Override
    public void onDisable() {
        final Server server = getServer();

        this.platform.stop();

        server.getGlobalRegionScheduler().cancelTasks(this);
        server.getAsyncScheduler().cancelTasks(this);
    }

    public final VaultSupport getSupport() {
        return this.support;
    }

    public final CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public final CrazyPlatform getPlatform() {
        return this.platform;
    }

    public final FusionPaper getFusion() {
        return this.fusion;
    }
}