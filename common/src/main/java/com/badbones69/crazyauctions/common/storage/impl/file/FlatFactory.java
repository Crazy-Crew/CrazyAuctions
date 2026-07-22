package com.badbones69.crazyauctions.common.storage.impl.file;

import com.badbones69.crazyauctions.common.CrazyAuctionsPlugin;
import com.badbones69.crazyauctions.common.storage.impl.ConnectionFactory;
import com.ryderbelserion.fusion.core.FusionCore;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class FlatFactory extends ConnectionFactory {

    protected final CrazyAuctionsPlugin plugin;
    protected final FusionCore fusion;

    private final String impl;

    public FlatFactory(final CrazyAuctionsPlugin plugin, final String impl) {
        this.fusion = plugin.getFusion();
        this.plugin = plugin;
        this.impl = impl;
    }

    @Override
    public String getImpl() {
        return this.impl;
    }
}