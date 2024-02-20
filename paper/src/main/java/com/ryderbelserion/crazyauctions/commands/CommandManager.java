package com.ryderbelserion.crazyauctions.commands;

import com.badbones69.crazyauctions.common.api.CrazyAuctionsPlugin;
import com.badbones69.crazyauctions.common.config.types.Config;
import com.ryderbelserion.cluster.utils.AdvUtils;
import com.ryderbelserion.crazyauctions.commands.engine.CommandContext;
import com.ryderbelserion.crazyauctions.commands.engine.CommandEngine;
import com.ryderbelserion.crazyauctions.commands.subs.HelpCommand;
import com.ryderbelserion.crazyauctions.commands.subs.ReloadCommand;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import org.jetbrains.annotations.NotNull;
import com.ryderbelserion.crazyauctions.CrazyAuctions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    @NotNull
    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final ConcurrentHashMap<String, CommandEngine> commands = new ConcurrentHashMap<>();

    private final LinkedList<CommandEngine> classes = new LinkedList<>();

    public void load() {
        // Create command config.
        CommandAPIBukkitConfig config = new CommandAPIBukkitConfig(this.plugin);
        config.shouldHookPaperReload(true).silentLogs(CrazyAuctionsPlugin.get().getConfig().getProperty(Config.verbose_logging)).setNamespace("crazyauctions");

        // Load command api.
        CommandAPI.onLoad(config);
    }

    public void enable() {
        // Enable command api.
        CommandAPI.onEnable();

        // Create default command.
        CommandAPICommand command = new CommandAPICommand("ca")
                .withAliases("ah")
                .withPermission("crazyauctions.help")
                .executes((sender, args) -> {
                    CommandContext context = new CommandContext(sender, args);

                    if (!context.isPlayer()) {
                        sender.sendMessage(AdvUtils.parse("<red>Must be a player."));
                        return;
                    }

                    sender.sendMessage("This is the base command.");

                    /*AuctionHouseMenu auctions = new AuctionHouseMenu(context.getPlayer(), 54, AuctionsFactory.getAuctions().getProperty(GuiConfig.inventory_name));
                    auctions.open();*/
                });

        // Bind subcommand to the object above.
        List.of(
                new ReloadCommand(command),
                new HelpCommand(command)
        ).forEach(this::addCommand);

        // Register it all.
        command.register();
    }

    public void addCommand(CommandEngine command) {
        this.commands.put(command.getLabel(), command);

        this.classes.add(command);

        command.registerSubCommand();
    }

    public Map<String, CommandEngine> getCommands() {
        return Collections.unmodifiableMap(this.commands);
    }

    public List<CommandEngine> getClasses() {
        return Collections.unmodifiableList(this.classes);
    }
}