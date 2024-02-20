package com.ryderbelserion.crazyauctions.commands.subs;

import com.badbones69.crazyauctions.common.enums.Messages;
import com.ryderbelserion.crazyauctions.commands.engine.CommandContext;
import dev.jorel.commandapi.CommandAPICommand;
import com.ryderbelserion.crazyauctions.commands.engine.CommandEngine;

public class ReloadCommand extends CommandEngine {

    public ReloadCommand(CommandAPICommand command) {
        super(command, "reload", "The reload command for CrazyAuctions", "crazyauctions.reload");
    }

    @Override
    public void execute(CommandContext context) {
        //this.plugin.getFactory().reload();

        Messages.plugin_reload.sendMessage(context.getSender());
    }
}