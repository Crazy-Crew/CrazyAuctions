package com.badbones69.crazyauctions.commands.features.relations;

import com.badbones69.crazyauctions.api.enums.Messages;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ArgumentRelations {

    private @NotNull final BukkitCommandManager<CommandSender> commandManager;

    public ArgumentRelations(@NotNull final BukkitCommandManager<CommandSender> commandManager) {
        this.commandManager = commandManager;
    }

    public void build() {
        this.commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> Messages.CORRECT_USAGE.sendMessage(sender, "%usage%", context.getSyntax()));

        this.commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> Messages.CORRECT_USAGE.sendMessage(sender, "%usage%", context.getSyntax()));

        this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> Messages.CORRECT_USAGE.sendMessage(sender, "%usage%", context.getSyntax()));

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> Messages.NO_PERMISSION.sendMessage(sender, "%permission%", context.getPermission().toString()));

        this.commandManager.registerMessage(BukkitMessageKey.UNKNOWN_COMMAND, (sender, context) -> Messages.UNKNOWN_COMMAND.sendMessage(sender, "%command%", context.getInvalidInput()));

        this.commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> Messages.MUST_BE_CONSOLE_SENDER.sendMessage(sender));

        this.commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> Messages.PLAYERS_ONLY.sendMessage(sender));
    }
}