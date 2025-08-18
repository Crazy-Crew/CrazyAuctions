package com.badbones69.crazyauctions.commands.features.player;

import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandHelp extends BaseCommand {

    @Command
    @Permission(value = "crazyauctions.access", def = PermissionDefault.TRUE)
    @Syntax("/crazyauctions")
    public void root(Player player) {
        view(player);
    }

    @Command(value = "help")
    @Permission(value = "crazyauctions.help", def = PermissionDefault.OP)
    @Syntax("/crazyauctions help")
    public void help(CommandSender sender) {
        sender.sendMessage(Messages.HELP.getMessage(sender));
    }
}