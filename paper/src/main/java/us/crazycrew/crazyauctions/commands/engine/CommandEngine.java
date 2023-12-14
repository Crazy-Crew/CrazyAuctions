package us.crazycrew.crazyauctions.commands.engine;

import dev.jorel.commandapi.CommandAPICommand;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.CrazyAuctions;

public abstract class CommandEngine {

    @NotNull
    protected final CrazyAuctions plugin = CrazyAuctions.get();

    // Root command.
    private final CommandAPICommand command;

    // Sub command.
    private CommandAPICommand subCommand;

    private final String label;
    private final String description;
    private final String permission;

    protected CommandEngine(CommandAPICommand command, String label, String description, String permission) {
        this.command = command;

        this.label = label;
        this.description = description;
        this.permission = permission;
    }

    public abstract void execute(CommandContext context);

    public void registerSubCommand() {
        this.subCommand = new CommandAPICommand(this.label).withShortDescription(this.description).withUsage("/" + this.command.getName() + " " + this.label).withPermission(this.permission).executes((sender, args) -> {
            CommandContext context = new CommandContext(sender, args);

            execute(context);
        });

        this.command.withSubcommand(this.subCommand);
    }

    public CommandAPICommand getCommand() {
        return this.command;
    }

    public CommandAPICommand getSubCommand() {
        return this.subCommand;
    }

    public String getLabel() {
        return this.label;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPermission() {
        return permission;
    }
}