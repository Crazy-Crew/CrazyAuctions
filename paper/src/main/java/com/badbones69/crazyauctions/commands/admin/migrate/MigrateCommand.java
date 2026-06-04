package com.badbones69.crazyauctions.commands.admin.migrate;

import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyauctions.commands.admin.migrate.types.LegacyConfigType;
import com.badbones69.crazyauctions.commands.admin.migrate.types.LegacyMessagesType;
import com.badbones69.crazyauctions.commands.arguments.MigrationArgument;
import com.badbones69.crazyauctions.common.enums.MigrationKey;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.constants.Messages;
import java.util.List;
import java.util.Optional;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class MigrateCommand extends BaseCommand {

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        final CommandSender sender = context.getSender();

        if (!context.isPlayer()) {
            this.adapter.sendMessage(sender, Messages.players_only);

            return;
        }

        final CommandContext<CommandSourceStack> source = context.getContext();

        final MigrationKey type = Optional.ofNullable(source.getArgument("type", MigrationKey.class)).orElse(MigrationKey.none);

        switch (type) {
            case legacy_messages -> new LegacyMessagesType(sender).run();

            case legacy_config -> new LegacyConfigType(sender).run();

            case none -> {}
        }
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("migrate").requires(this::requirement);

        final RequiredArgumentBuilder<CommandSourceStack, MigrationKey> arg1 = argument("type", new MigrationArgument()).executes(context -> {
            run(new PaperCommandContext(context));

            return Command.SINGLE_SUCCESS;
        });

        return root.then(arg1).build();
    }

    @Override
    public @NotNull final List<PermissionContext> getPermissions() {
        return List.of(Permissions.view.getContext());
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        return context.getSender().hasPermission(getPermissions().getFirst().getPermission());
    }
}