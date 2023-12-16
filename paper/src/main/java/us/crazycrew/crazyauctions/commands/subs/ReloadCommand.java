package us.crazycrew.crazyauctions.commands.subs;

import com.badbones69.crazyauctions.common.enums.Messages;
import dev.jorel.commandapi.CommandAPICommand;
import us.crazycrew.crazyauctions.commands.engine.CommandContext;
import us.crazycrew.crazyauctions.commands.engine.CommandEngine;

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