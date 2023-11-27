package com.badbones69.crazyauctions.paper.api.support.metrics;

import com.badbones69.crazyauctions.paper.CrazyAuctions;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.NotNull;

public class MetricsWrapper {

    @NotNull
    private final CrazyAuctions plugin = CrazyAuctions.get();

    private Metrics metrics;

    public void start() {
        if (this.metrics != null) {
            if (this.plugin.isLogging()) this.plugin.getLogger().warning("Metrics is already enabled.");
            return;
        }

        this.metrics = new Metrics(this.plugin, 4624);

        if (this.plugin.isLogging()) this.plugin.getLogger().fine("Metrics has been enabled.");
    }

    public void stop() {
        if (this.metrics == null) {
            if (this.plugin.isLogging()) this.plugin.getLogger().warning("Metrics isn't enabled so we do nothing.");
            return;
        }

        this.metrics.shutdown();
        this.metrics = null;

        if (this.plugin.isLogging()) this.plugin.getLogger().fine("Metrics has been turned off.");
    }
}