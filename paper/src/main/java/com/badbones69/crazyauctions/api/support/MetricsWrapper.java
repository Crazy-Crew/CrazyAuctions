package com.badbones69.crazyauctions.api.support;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bstats.bukkit.Metrics;

public class MetricsWrapper {

    /**
     * Creates a new Metrics instance.
     *
     * @param serviceId The id of the service. It can be found at <a href="https://bstats.org/what-is-my-plugin-id">What is my plugin id?</a>
     */
    public MetricsWrapper(final CrazyAuctions plugin, final int serviceId) {
        new Metrics(plugin, serviceId);
    }
}