package us.crazycrew.crazyauctions.commands.subs;

import dev.jorel.commandapi.CommandAPICommand;
import us.crazycrew.crazyauctions.commands.engine.CommandContext;
import us.crazycrew.crazyauctions.commands.engine.CommandEngine;
import us.crazycrew.crazyauctions.commands.engine.CommandHelpEntry;

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