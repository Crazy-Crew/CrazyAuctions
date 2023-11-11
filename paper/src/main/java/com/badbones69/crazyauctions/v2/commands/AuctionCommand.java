package com.badbones69.crazyauctions.commands;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.frame.command.CommandContext;
import com.badbones69.crazyauctions.api.frame.command.CommandEngine;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Collections;

public class AuctionCommand extends CommandEngine {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    protected AuctionCommand() {
        super("auctions", "Opens the auction house", "/crazyauctions:auctions", Collections.emptyList());
    }

    @Override
    protected void perform(CommandContext context, String[] args) {

    }
}