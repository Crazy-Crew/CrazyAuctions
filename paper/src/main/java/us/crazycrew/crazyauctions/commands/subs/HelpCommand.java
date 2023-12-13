package us.crazycrew.crazyauctions.commands.subs;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import us.crazycrew.crazyauctions.commands.engine.CommandEngine;
import java.util.Collections;

public class HelpCommand extends CommandEngine {

    public HelpCommand(CommandAPICommand command) {
        super(command, "help", "The help command for CrazyAuctions", "crazyauctions.help", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

    }
}