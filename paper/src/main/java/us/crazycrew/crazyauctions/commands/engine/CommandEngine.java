package us.crazycrew.crazyauctions.commands.engine;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.CrazyAuctions;
import java.util.List;

public abstract class CommandEngine {

    @NotNull
    protected final CrazyAuctions plugin = CrazyAuctions.get();

    private final CommandAPICommand command;
    private final String label;
    private final String description;
    private final String permission;
    private final List<String> aliases;

    protected CommandEngine(CommandAPICommand command, String label, String description, String permission, List<String> aliases) {
        this.command = command;

        this.label = label;
        this.description = description;
        this.permission = permission;
        this.aliases = aliases;
    }
    public abstract void execute(CommandSender sender, CommandArguments args);

    public void registerSubCommand() {
        CommandAPICommand subCommand = new CommandAPICommand(this.label).withShortDescription(this.description).withPermission(this.permission).executes(this::execute);

        this.command.withSubcommand(subCommand);
    }

    public String getLabel() {
        return this.label;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getAliases() {
        return this.aliases;
    }
}