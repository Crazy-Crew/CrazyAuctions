package com.badbones69.crazyauctions.commands.features;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.commands.features.admin.CommandCancel;
import com.badbones69.crazyauctions.commands.features.admin.CommandReload;
import com.badbones69.crazyauctions.commands.features.admin.migrate.CommandMigrate;
import com.badbones69.crazyauctions.commands.features.player.CommandExpired;
import com.badbones69.crazyauctions.commands.features.player.CommandHelp;
import com.badbones69.crazyauctions.commands.features.player.CommandListed;
import com.badbones69.crazyauctions.commands.features.player.CommandView;
import com.badbones69.crazyauctions.commands.features.player.auction.CommandBid;
import com.badbones69.crazyauctions.commands.features.player.auction.CommandSell;
import com.badbones69.crazyauctions.commands.features.relations.ArgumentRelations;
import com.ryderbelserion.fusion.paper.api.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    private @NotNull final CrazyAuctions plugin = CrazyAuctions.get();

    private @NotNull final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private @NotNull final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this.plugin);

    public CommandHandler() {
        load();
    }

    public void load() {
        final Server server = this.plugin.getServer();

        final PluginManager pluginManager = server.getPluginManager();

        new ArgumentRelations(this.commandManager).build();

        this.commandManager.registerSuggestion(SuggestionKey.of("players"), sender -> server.getOnlinePlayers().stream().map(Player::getName).toList());

        this.commandManager.registerSuggestion(SuggestionKey.of("numbers"), sender -> {
            final List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 100; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        this.commandManager.registerSuggestion(SuggestionKey.of("doubles"), sender -> {
            final List<String> numbers = new ArrayList<>();

            int count = 0;

            while (count <= 1000) {
                double x = count / 10.0;

                numbers.add(String.valueOf(x));

                count++;
            }

            return numbers;
        });

        this.commandManager.registerSuggestion(SuggestionKey.of("price"), sender -> List.of("50", "100", "250", "500", "1000", "2500", "5000", "10000"));
        this.commandManager.registerSuggestion(SuggestionKey.of("amount"), sender -> List.of("1", "2", "4", "8", "10", "20", "40", "64"));

        this.commandManager.registerArgument(PlayerBuilder.class, (sender, context) -> new PlayerBuilder(server, context));

        List.of(
                new CommandMigrate(),
                new CommandReload(),
                new CommandCancel(),

                new CommandExpired(),
                new CommandListed(),
                new CommandView(),
                new CommandHelp(),
                new CommandSell(),
                new CommandBid()
        ).forEach(this.commandManager::registerCommand);

        /*for (final PermissionKeys key : PermissionKeys.values()) {
            final String node = key.getPermission();

            final Permission current = pluginManager.getPermission(node);

            if (current != null) continue;

            final Permission permission = new Permission(
                    node,
                    key.getDescription(),
                    key.isDefault(),
                    key.getChildren()
            );

            pluginManager.addPermission(permission);
        }*/
    }

    public @NotNull final BukkitCommandManager<CommandSender> getCommandManager() {
        return this.commandManager;
    }
}