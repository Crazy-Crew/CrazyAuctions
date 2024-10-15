package com.badbones69.crazyauctions.commands.v2.staff;

import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.commands.v2.AbstractCommand;
import com.badbones69.crazyauctions.configs.ConfigManager;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.vital.paper.commands.context.PaperCommandInfo;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandReload extends AbstractCommand {

    @Override
    public void execute(final PaperCommandInfo info) {
        this.server.getGlobalRegionScheduler().cancelTasks(this.plugin);
        this.server.getAsyncScheduler().cancelTasks(this.plugin);

        ConfigManager.refresh();

        this.plugin.getFileManager().reloadFiles();

        this.crazyManager.load();

        final CommandSender sender = info.getCommandSender();

        sender.sendMessage(Messages.RELOAD.getMessage(sender));
    }

    @Override
    public @NotNull final String getPermission() {
        return Permissions.reload.getNode();
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        return Commands.literal("reload")
                .requires(source -> source.getSender().hasPermission(getPermission()))
                .executes(context -> {
                    execute(new PaperCommandInfo(context));

                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                }).build();
    }

    @Override
    public @NotNull final AbstractCommand registerPermission() {
        Permissions.reload.registerPermission();

        return this;
    }
}