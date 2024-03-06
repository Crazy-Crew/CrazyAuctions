package com.ryderbelserion.crazyauctions;

import com.ryderbelserion.cluster.ClusterPackage;
import com.ryderbelserion.crazyauctions.platform.PaperServer;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyAuctionsPaper extends JavaPlugin {

    private CrazyAuctions crazyAuctions;

    @Override
    public void onEnable() {
        this.crazyAuctions = new CrazyAuctions(new PaperServer());

        new ClusterPackage(this, true);
    }

    @Override
    public void onDisable() {
        if (this.crazyAuctions != null) {
            this.crazyAuctions.disable();
        }
    }

    public CrazyAuctions getCrazyAuctions() {
        return this.crazyAuctions;
    }
}