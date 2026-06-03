package com.badbones69.crazyauctions.commands.admin.auction;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.Reasons;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyauctions.common.enums.FileKeys;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.constants.Messages;
import java.util.List;
import java.util.UUID;

public class ForceCancelCommand extends BaseCommand {

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        final CommandSender sender = context.getSender();

        final YamlConfiguration data = FileKeys.data.getConfiguration();

        int number = 1;

        final ConfigurationSection section = data.getConfigurationSection("Items");

        if (section == null) {
            return;
        }

        for (final String id : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(id);

            if (item == null) continue;

            final String seller = item.getString("Seller", "");

            if (seller.isBlank()) continue;

            final UUID uuid = UUID.fromString(seller);

            final Player entity = this.server.getPlayer(uuid);

            if (entity != null) {
                this.adapter.sendMessage(entity, Messages.admin_force_cancelled_to_player);
            }

            number = Methods.expireItem(number, this.server.getOfflinePlayer(uuid), id, data, Reasons.ADMIN_FORCE_CANCEL);
        }

        FileKeys.data.save();

        this.adapter.sendMessage(sender, Messages.admin_force_cancelled_all);
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        return Commands.literal("force_end_all").requires(this::requirement)
                .executes(context -> {
                    run(new PaperCommandContext(context));

                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    @Override
    public @NotNull final List<PermissionContext> getPermissions() {
        return List.of(Permissions.force_end.getContext());
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        return context.getSender().hasPermission(getPermissions().getFirst().getPermission());
    }
}