package com.badbones69.crazyauctions.api.frame.command;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.frame.command.builders.CommandDataEntry;
import com.badbones69.crazyauctions.api.frame.command.builders.args.Argument;
import com.badbones69.crazyauctions.frame.CrazyCore;
import com.badbones69.crazyauctions.frame.utils.AdventureUtils;
import com.badbones69.crazyauctions.support.PlaceholderSupport;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public abstract class CommandEngine extends Command {

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private final LinkedList<String> labels = new LinkedList<>();
    public final LinkedList<Argument> requiredArgs = new LinkedList<>();
    public final LinkedList<Argument> optionalArgs = new LinkedList<>();

    private final HashMap<String, CommandDataEntry> commandData = new HashMap<>();

    protected CommandEngine(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    public void execute(CommandContext context, String[] args) {
        perform(context, args);
    }

    public void execute(CommandContext context) {
        StringBuilder label = new StringBuilder(context.getLabel());

        if (!context.getArgs().isEmpty()) {
            for (CommandEngine engine : this.plugin.getCommandManager().getClasses()) {
                boolean isPresent = context.getArgs().stream().findFirst().isPresent();

                if (isPresent) {
                    label.append(" ").append(context.getArgs().get(0));

                    context.removeArgs(0);
                    context.setLabel(label.toString());

                    engine.execute(context);
                    return;
                }
            }
        }

        if (!validate(context)) return;

        perform(context, new String[0]);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        List<String> arguments = Arrays.asList(args);

        CommandContext context = new CommandContext(
                sender,
                label,
                arguments
        );

        if (arguments.isEmpty()) {
            execute(context);
            return true;
        }

        execute(context, args);

        return true;
    }

    protected abstract void perform(CommandContext context, String[] args);

    private boolean validate(CommandContext context) {
        if (context.getArgs().size() < this.requiredArgs.size()) {
            context.reply(CrazyCore.api().commandTooFewArgs());
            sendValidFormat(context);
            return false;
        }

        if (context.getArgs().size() > this.requiredArgs.size() + this.optionalArgs.size()) {
            context.reply(CrazyCore.api().commandTooManyArgs());
            sendValidFormat(context);
            return false;
        }

        return true;
    }

    private void sendValidFormat(CommandContext context) {
        ArrayList<Argument> arguments = new ArrayList<>();

        arguments.addAll(this.requiredArgs);
        arguments.addAll(this.optionalArgs);

        this.requiredArgs.sort(Comparator.comparing(Argument::order));

        if (context.isPlayer()) {
            StringBuilder format = new StringBuilder("/" + "crazycrates:" + getLabel());

            TextComponent.@NotNull Builder emptyComponent = Component.text();

            StringBuilder types = new StringBuilder();

            for (Argument arg : arguments) {
                String value = this.optionalArgs.contains(arg) ? " (" + arg.name() + ") " : " <" + arg.name() + ">";

                String msg = this.optionalArgs.contains(arg) ? CrazyCore.api().commandOptionalMsg() : CrazyCore.api().commandRequiredMsg();

                Component argComponent = AdventureUtils.parse(value).hoverEvent(HoverEvent.showText(AdventureUtils.parse(PlaceholderSupport.setPlaceholders(msg)))).asComponent();

                emptyComponent.append(argComponent);

                boolean isPresent = arg.argumentType().getPossibleValues().stream().findFirst().isPresent();

                if (isPresent) types.append(" ").append(arg.argumentType().getPossibleValues().stream().findFirst().get());
            }

            TextComponent.@NotNull Builder finalComponent = emptyComponent
                    .hoverEvent(HoverEvent.showText(AdventureUtils.parse("<gold>Click me to insert into chat</gold>")))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, format.append(types).toString()))
                    .append(emptyComponent.build());

            context.reply(finalComponent.build());

            return;
        }

        StringBuilder format = new StringBuilder("/" + "crazycrates:" + getLabel());

        for (Argument arg : arguments) {
            String value = this.optionalArgs.contains(arg) ? "(" + arg.name() + ") " : "<" + arg.name() + "> ";

            format.append(value);
        }

        context.reply(format.toString());
    }

    public List<String> getLabels() {
        return Collections.unmodifiableList(this.labels);
    }

    public Map<String, CommandDataEntry> getCommandData() {
        return Collections.unmodifiableMap(this.commandData);
    }

    public List<Argument> getRequiredArgs() {
        return Collections.unmodifiableList(this.requiredArgs);
    }

    public List<Argument> getOptionalArgs() {
        return Collections.unmodifiableList(this.optionalArgs);
    }
}