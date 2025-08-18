package com.badbones69.crazyauctions.commands.features.admin;

import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandReload extends BaseCommand {

    @Command(value = "reload")
    @Permission(value = "crazyauctions.reload", def = PermissionDefault.OP)
    @Syntax("/crazyauctions reload")
    public void reload(CommandSender sender) {
        this.fileManager.refresh(true);

        this.crazyManager.load();

        sender.sendMessage(Messages.RELOAD.getMessage(sender));
    }
}