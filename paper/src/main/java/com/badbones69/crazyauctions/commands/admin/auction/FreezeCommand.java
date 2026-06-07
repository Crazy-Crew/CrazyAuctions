package com.badbones69.crazyauctions.commands.admin.auction;

import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.commands.BaseCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.enums.server.ServerState;
import java.util.List;

public class FreezeCommand extends BaseCommand {

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        final CommandSender sender = context.getSender();

        if (this.serverAdapter.hasState(ServerState.auctions_frozen)) {
            sender.sendRichMessage("<red>All online players can no longer interact, or bid/sell!");

            this.serverAdapter.removeState(ServerState.auctions_frozen);

            return;
        }

        sender.sendRichMessage("<red>All online players can now interact, and bid/sell again!");

        this.serverAdapter.addState(ServerState.auctions_frozen);
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        return Commands.literal("freeze").requires(this::requirement)
                .executes(context -> {
                    run(new PaperCommandContext(context));

                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    @Override
    public @NotNull final List<PermissionContext> getPermissions() {
        return List.of(Permissions.freeze.getContext());
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        return context.getSender().hasPermission(getPermissions().getFirst().getPermission());
    }
}