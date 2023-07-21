package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.frame.PaperCore;
import com.badbones69.crazyauctions.api.frame.command.CommandManager;
import com.badbones69.crazyauctions.frame.CrazyLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyAuctions extends JavaPlugin {

    private final ApiManager apiManager;
    private final PaperCore paperCore;
    private CommandManager commandManager;

    public CrazyAuctions(ApiManager apiManager, PaperCore paperCore) {
        this.apiManager = apiManager;
        this.paperCore = paperCore;
    }

    @Override
    public void onEnable() {
        this.commandManager = CommandManager.create();
    }

    @Override
    public void onDisable() {
        CrazyLogger.debug("Dick");
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
}