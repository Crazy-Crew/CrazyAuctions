package com.ryderbelserion.crazyauctions.commands.subs;

import com.ryderbelserion.crazyauctions.commands.engine.CommandContext;
import com.ryderbelserion.crazyauctions.commands.engine.CommandEngine;
import com.ryderbelserion.crazyauctions.commands.engine.CommandHelpEntry;
import dev.jorel.commandapi.CommandAPICommand;

public class HelpCommand extends CommandEngine {

    public HelpCommand(CommandAPICommand command) {
        super(command, "help", "The help command for CrazyAuctions", "crazyauctions.help");
    }

    @Override
    public void execute(CommandContext context) {
        CommandHelpEntry entry = new CommandHelpEntry();

        entry.help(context);
    }
}