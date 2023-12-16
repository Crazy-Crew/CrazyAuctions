package com.badbones69.crazyauctions.common.api.interfaces;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyauctions.common.api.CrazyAuctionsPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public abstract class AbstractPlugin {

    public abstract File getDataFolder();

    public abstract void init();

    public abstract void stop();

    public abstract void reload();

    public abstract SettingsManager getConfig();

    public abstract SettingsManager getLocale();

    public void enable() {
        CrazyAuctionsPlugin.init(this);
    }

    public void disable() {
        CrazyAuctionsPlugin.stop();
    }

    @NotNull
    public YamlFileResourceOptions indent() {
        return YamlFileResourceOptions.builder().indentationSize(2).build();
    }
}