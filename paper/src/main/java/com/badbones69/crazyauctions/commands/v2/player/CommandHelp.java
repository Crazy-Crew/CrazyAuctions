package com.badbones69.crazyauctions.commands.v2.player;

import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.commands.v2.AbstractCommand;
import com.badbones69.crazyauctions.configs.impl.locale.MiscKeys;
import com.badbones69.crazyauctions.utils.MsgUtils;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.vital.paper.commands.context.PaperCommandInfo;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class CommandHelp extends AbstractCommand {

    @Override
    public void execute(final PaperCommandInfo info) {
        final int page = info.getIntegerArgument("page");

        final CommandSender sender = info.getCommandSender();

        final Map<String, List<String>> help = this.userManager.getUser(sender).getLocale().getProperty(MiscKeys.help).getEntry();

        help.get(String.valueOf(page)).forEach(line -> MsgUtils.sendMessage(sender, line, "{max}", String.valueOf(help.size())));
    }

    @Override
    public @NotNull final String getPermission() {
        return Permissions.help.getNode();
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("help").requires(source -> source.getSender().hasPermission(getPermission()));

        final RequiredArgumentBuilder<CommandSourceStack, Integer> arg1 = argument("page", IntegerArgumentType.integer()).suggests((ctx, builder) -> {
            this.userManager.getUser(ctx.getSource().getSender()).getLocale().getProperty(MiscKeys.help).getEntry().keySet().forEach(builder::suggest);

            return builder.buildFuture();
        }).executes(context -> {
            execute(new PaperCommandInfo(context));

            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        });

        return root.then(arg1).build();
    }

    @Override
    public @NotNull final AbstractCommand registerPermission() {
        Permissions.help.registerPermission();

        return this;
    }
}