package com.badbones69.crazyauctions.api.support;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.ryderbelserion.vital.paper.api.bStats;

public class MetricsWrapper extends bStats {

    /**
     * Creates a new Metrics instance.
     *
     * @param serviceId The id of the service. It can be found at <a href="https://bstats.org/what-is-my-plugin-id">What is my plugin id?</a>
     */
    public MetricsWrapper(final CrazyAuctions plugin, final int serviceId) {
        super(plugin, serviceId);
    }
}