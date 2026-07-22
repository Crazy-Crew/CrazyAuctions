package com.badbones69.crazyauctions.commands;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.CrazyAuctionsPaper;
import com.badbones69.crazyauctions.api.registry.adapters.PaperSenderAdapter;
import com.badbones69.crazyauctions.common.storage.holder.StorageHolder;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.commands.PaperCommand;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import java.util.Optional;

public abstract class BaseCommand extends PaperCommand {

    protected final CrazyAuctions plugin = CrazyAuctions.get();

    protected final CrazyAuctionsPaper platform = this.plugin.getPlatform();

    protected final VaultSupport support = this.plugin.getSupport();

    protected final StorageHolder holder = this.platform.getStorageHolder();

    protected final FusionPaper fusion = this.platform.getFusion();

    protected final PaperSenderAdapter adapter = this.platform.getSenderAdapter();

    protected final Server server = this.plugin.getServer();

    protected @NonNull Optional<Player> getPlayer(@NonNull final CommandContext<CommandSourceStack> context) {
        Player player = null;

        final PlayerSelectorArgumentResolver resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);

        try {
             player = resolver.resolve(context.getSource()).getFirst();
        } catch (final CommandSyntaxException exception) {
            exception.printStackTrace();
        }

        return Optional.ofNullable(player);
    }
}