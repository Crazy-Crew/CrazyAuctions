package us.crazycrew.crazyauctions.commands;

import com.badbones69.crazyauctions.common.api.CrazyAuctionsPlugin;
import com.badbones69.crazyauctions.common.config.types.Config;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.CrazyAuctions;
import us.crazycrew.crazyauctions.commands.engine.CommandEngine;
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
        config.shouldHookPaperReload(true).silentLogs(!CrazyAuctionsPlugin.get().getConfig().getProperty(Config.verbose_logging));

        // Load command api.
        CommandAPI.onLoad(config);
    }

    public void enable() {
        // Enable command api.
        CommandAPI.onEnable();

        // Create default command.
        /*CommandAPICommand command = new CommandAPICommand("crazyauctions")
                .withAliases("ca", "ah")
                .withPermission("crazyauctions.help")
                .executes((sender, args) -> {
                    CommandContext context = new CommandContext(sender, args);

                    if (!context.isPlayer()) {
                        sender.sendMessage(AdvUtils.parse("<red>Must be a player."));
                        return;
                    }

                    AuctionHouseMenu auctions = new AuctionHouseMenu(context.getPlayer(), 54, AuctionsFactory.getAuctions().getProperty(GuiConfig.inventory_name));
                    auctions.open();
                });

        // Bind subcommand to the object above.
        List.of(
                new ReloadCommand(command),
                new HelpCommand(command)
        ).forEach(this::addCommand);

        // Register it all.
        command.register();*/
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