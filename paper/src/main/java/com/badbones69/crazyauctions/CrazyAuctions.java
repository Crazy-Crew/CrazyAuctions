package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.frame.PaperCore;
import com.badbones69.crazyauctions.api.frame.command.CommandManager;
import com.badbones69.crazyauctions.commands.inventories.AuctionInventoryClick;
import com.badbones69.crazyauctions.events.DataListener;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyAuctions extends JavaPlugin {

    private final ApiManager apiManager;
    private final PaperCore paperCore;
    private CommandManager commandManager;

    private CrazyManager crazyManager;

    public CrazyAuctions(ApiManager apiManager, PaperCore paperCore) {
        this.apiManager = apiManager;
        this.paperCore = paperCore;
    }

    @Override
    public void onEnable() {
        this.commandManager = CommandManager.create();

        this.crazyManager = new CrazyManager();
        this.crazyManager.load(true);

        getServer().getPluginManager().registerEvents(new DataListener(), this);
        getServer().getPluginManager().registerEvents(new AuctionInventoryClick(), this);
    }

    @Override
    public void onDisable() {
        if (this.crazyManager != null) this.crazyManager.stop();
    }

    public ApiManager getApiManager() {
        return this.apiManager;
    }

    public PaperCore getPaperCore() {
        return this.paperCore;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }
}