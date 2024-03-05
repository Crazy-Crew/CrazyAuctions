package com.ryderbelserion.crazyauctions.platform;

import com.ryderbelserion.crazyauctions.CrazyAuctionsPaper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public class PaperServer implements Server {

    @NotNull
    private final CrazyAuctionsPaper plugin = JavaPlugin.getPlugin(CrazyAuctionsPaper.class);

    @Override
    public File getFolder() {
        return this.plugin.getDataFolder();
    }
}