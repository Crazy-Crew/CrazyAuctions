package com.badbones69.crazyauctions.commands;

import com.badbones69.crazyauctions.api.builders.gui.types.AuctionMenu;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.common.enums.FileKey;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
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

        //final FileConfiguration config = FileKey.config.getConfiguration();

        final Player player = context.getPlayer();

        /*if (config.getBoolean("Settings.Category-Page-Opens-First", false)) {
            GuiListener.openCategories(player, ShopType.SELL);

            return;
        }*/

        final AuctionMenu menu = new AuctionMenu(player, FileKey.auction.getYamlConfig());

        final ShopType shopType = this.platform.isSellModuleEnabled() ? ShopType.SELL : ShopType.BID;

        menu.build(shopType);
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