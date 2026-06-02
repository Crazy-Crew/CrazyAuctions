package com.badbones69.crazyauctions.commands.player;

import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ExpiredCommand extends BaseCommand {

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        final CommandSender sender = context.getSender();

        if (!context.isPlayer()) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage(sender));

            return;
        }

        GuiListener.openPlayersExpiredList(context.getPlayer(), 1);
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        return Commands.literal("expired").requires(this::requirement)
                .executes(context -> {
                    run(new PaperCommandContext(context));

                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    @Override
    public @NotNull final List<PermissionContext> getPermissions() {
        return List.of(
                Permissions.expired.getContext(),
                Permissions.access.getContext()
        );
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        boolean hasPermission = false;

        final CommandSender sender = context.getSender();

        for (final PermissionContext permissionContext : getPermissions()) {
            final String permission = permissionContext.getPermission();

            hasPermission = sender.hasPermission(permission);

            if (hasPermission) {
                break;
            }
        }

        return hasPermission;
    }
}