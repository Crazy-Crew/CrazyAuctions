package com.badbones69.crazyauctions.commands.v2;

import com.badbones69.crazyauctions.api.builders.types.AuctionMenu;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.configs.impl.gui.AuctionKeys;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.vital.paper.commands.context.PaperCommandInfo;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public class BaseCommand extends AbstractCommand {

    @Override
    public void execute(final PaperCommandInfo info) {
        if (!info.isPlayer()) {
            //todo() send message to require as a player.

            return;
        }

        new AuctionMenu(info.getPlayer(), this.auctions.getProperty(AuctionKeys.gui_name), 6).open();
    }

    @Override
    public @NotNull final String getPermission() {
        return Permissions.use.getNode();
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        return Commands.literal("crazyauctions")
                .requires(source -> source.getSender().hasPermission(getPermission()))
                .executes(context -> {
                    execute(new PaperCommandInfo(context));

                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                }).build();
    }

    @Override
    public @NotNull final AbstractCommand registerPermission() {
        Permissions.use.registerPermission();

        return this;
    }
}