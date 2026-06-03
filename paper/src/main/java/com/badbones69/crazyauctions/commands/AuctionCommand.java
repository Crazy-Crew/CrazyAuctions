package com.badbones69.crazyauctions.commands;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.common.enums.FileKeys;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.constants.Messages;
import us.crazycrew.api.enums.ShopType;
import java.util.List;

public class AuctionCommand extends BaseCommand {

    @Override
    public void run(@NonNull final PaperCommandContext context) {
        final CommandSender sender = context.getSender();

        if (!context.isPlayer()) {
            this.adapter.sendMessage(sender, Messages.players_only);

            return;
        }

        final FileConfiguration config = FileKeys.config.getConfiguration();

        final Player player = context.getPlayer();

        if (config.getBoolean("Settings.Category-Page-Opens-First", false)) {
            GuiListener.openCategories(player, ShopType.SELL);

            return;
        }

        if (this.platform.isSellModuleEnabled()) {
            GuiListener.openShop(player, ShopType.SELL, Category.NONE, 1);
        } else if (this.platform.isBidModuleEnabled()) {
            GuiListener.openShop(player, ShopType.BID, Category.NONE, 1);
        } else {
            player.sendMessage(Methods.getPrefix() + Methods.color("&cThe bidding and selling options are both disabled. Please contact the admin about this."));
        }
    }

    @Override
    public @NonNull final List<PermissionContext> getPermissions() {
        return List.of(Permissions.access.getContext());
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        return context.getSender().hasPermission(getPermissions().getFirst().getPermission());
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        return Commands.literal("crazyauctions").executes(context -> {
            run(new PaperCommandContext(context));

            return Command.SINGLE_SUCCESS;
        }).requires(this::requirement).build();
    }

    @Override
    public @NonNull final List<String> getAliases() {
        return List.of("ah", "ca");
    }
}