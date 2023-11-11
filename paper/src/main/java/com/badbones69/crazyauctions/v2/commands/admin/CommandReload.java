package com.badbones69.crazyauctions.commands.admin;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.frame.command.CommandContext;
import com.badbones69.crazyauctions.api.frame.command.CommandEngine;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Collections;

public class CommandReload extends CommandEngine {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);
    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    public CommandReload() {
        super("reload", "Reloads the plugin.", "/crazyauctions:reload", Collections.emptyList());
    }

    @Override
    protected void perform(CommandContext context, String[] args) {
        this.crazyManager.reload();
    }
}