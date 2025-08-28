package com.badbones69.crazyauctions.v2.commands;

import com.badbones69.crazyauctions.commands.features.admin.migrate.enums.MigrationType;
import com.badbones69.crazyauctions.commands.features.relations.ArgumentRelations;
import com.badbones69.crazyauctions.v2.CrazyAuctionsPlus;
import com.badbones69.crazyauctions.v2.commands.types.player.CommandHelp;
import com.ryderbelserion.fusion.paper.api.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    private @NotNull final CrazyAuctionsPlus plugin = JavaPlugin.getPlugin(CrazyAuctionsPlus.class);

    private @NotNull final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this.plugin);

    public CommandHandler() {
        load();
    }

    public void load() {
        final Server server = this.plugin.getServer();

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

        this.commandManager.registerSuggestion(SuggestionKey.of("migrators"), sender -> {
            final List<String> migrators = new ArrayList<>();

            for (MigrationType value : MigrationType.values()) {
                final String name = value.getName();

                migrators.add(name);
            }

            return migrators;
        });

        this.commandManager.registerArgument(PlayerBuilder.class, (sender, context) -> new PlayerBuilder(server, context));

        List.of(
                new CommandHelp()
        ).forEach(this.commandManager::registerCommand);
    }

    public @NotNull final BukkitCommandManager<CommandSender> getCommandManager() {
        return this.commandManager;
    }
}