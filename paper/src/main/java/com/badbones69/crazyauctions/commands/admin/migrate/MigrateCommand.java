package com.badbones69.crazyauctions.commands.admin.migrate;

import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyauctions.commands.arguments.MigrationArgument;
import com.badbones69.crazyauctions.common.enums.FileKeys;
import com.badbones69.crazyauctions.common.enums.MigrationKeys;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.message.adapter.interfaces.IMessageAdapter;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.kyori.utils.AdvUtils;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
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

        final MigrationKeys type = Optional.ofNullable(source.getArgument("type", MigrationKeys.class)).orElse(MigrationKeys.none);

        switch (type) {
            case legacy_messages -> {
                final CommentedConfigurationNode configuration = FileKeys.messages.getYamlConfig();

                final CommentedConfigurationNode messages = configuration.node("Messages");

                messages.childrenMap().forEach((id, _) -> {
                    final Optional<IMessageAdapter> adapter = this.messageRegistry.getMessage(FusionKey.key(this.fusion.getNamespace(), String.valueOf(id).toLowerCase().replace("-", "_")));

                    final CommentedConfigurationNode section = messages.node(id);

                    try {
                        section.set(String.class, AdvUtils.convert(section.getString(adapter.map(IMessageAdapter::getValue).orElse(""))));
                    } catch (final SerializationException exception) {
                        exception.printStackTrace();
                    }
                });

                FileKeys.messages.save();

                this.messageImpl.reload();
            }

            case legacy_config -> {
                final YamlConfiguration configuration = FileKeys.config.getConfiguration();

                configuration.set("Settings.Prefix", AdvUtils.convert(configuration.getString("Settings.Prefix", "<gray>[<dark_red>Crazy <aqua>Auctions<gray>]: ")));

                FileKeys.config.save();
            }

            case none -> {}
        }
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("migrate").requires(this::requirement);

        final RequiredArgumentBuilder<CommandSourceStack, MigrationKeys> arg1 = argument("type", new MigrationArgument()).executes(context -> {
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