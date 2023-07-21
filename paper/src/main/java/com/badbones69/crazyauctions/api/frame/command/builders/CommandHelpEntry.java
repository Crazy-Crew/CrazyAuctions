package com.badbones69.crazyauctions.api.frame.command.builders;

import com.badbones69.crazyauctions.ApiManager;
import com.badbones69.crazyauctions.api.frame.command.CommandEngine;
import com.badbones69.crazyauctions.api.frame.command.CommandManager;
import com.badbones69.crazyauctions.api.frame.command.builders.args.Argument;
import com.badbones69.crazyauctions.api.frame.command.builders.other.ComponentBuilder;
import com.badbones69.crazyauctions.config.types.PluginConfig;
import com.badbones69.crazyauctions.frame.CrazyCore;
import com.badbones69.crazyauctions.support.PlaceholderSupport;
import net.kyori.adventure.text.event.ClickEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import static com.badbones69.crazyauctions.frame.utils.AdventureUtils.hover;
import static com.badbones69.crazyauctions.frame.utils.AdventureUtils.send;

public class CommandHelpEntry {

    private final CommandManager manager;
    private final CommandActor actor;

    private int page = 1;
    private int perPage;
    private int totalPages;
    private int totalResults;
    private boolean lastPage;

    public CommandHelpEntry(CommandManager manager, CommandActor actor) {
        this.manager = manager;
        this.actor = actor;

        this.perPage = manager.defaultHelpPerPage();
    }

    public void showHelp() {
        this.showHelp(this.actor);
    }

    public void showHelp(CommandActor actor) {
        int min = this.perPage * (this.page - 1);
        int max = min + this.perPage;

        this.totalResults = this.manager.getCommands().size();

        this.totalPages = this.totalResults / this.perPage;

        if (min >= this.totalResults) {
            actor.reply(ApiManager.getPluginConfig().getProperty(PluginConfig.INVALID_HELP_PAGE).replaceAll("\\{page}", String.valueOf(page)));
            return;
        }

        Map<String, CommandDataEntry> entries = this.manager.getCommands();

        for (int value = min; value < max; value++) {
            if (this.totalResults - 1 < value) continue;

            CommandEngine command = this.manager.getClasses().get(value);

            CommandDataEntry dataEntry = entries.get(command.getLabel());

            boolean isHidden = dataEntry.isHidden();

            StringBuilder baseFormat = new StringBuilder("/" + command.getLabel());

            String format = CrazyCore.api().commandPageFormat()
                    .replaceAll("\\{command}", baseFormat.toString())
                    .replaceAll("\\{description}", command.getDescription());

            // Only add aliases if the list isn't empty.
            if (!command.getAliases().isEmpty()) baseFormat.append(" ").append(command.getLabels().get(0));

            ArrayList<Argument> arguments = new ArrayList<>();

            arguments.addAll(command.getOptionalArgs());
            arguments.addAll(command.getRequiredArgs());

            arguments.sort(Comparator.comparingInt(Argument::order));

            if (actor.isPlayer()) {
                StringBuilder types = new StringBuilder();

                ComponentBuilder builder = new ComponentBuilder();

                for (Argument arg : arguments) {
                    String argValue = command.optionalArgs.contains(arg) ? " (" + arg.name() + ") " : " <" + arg.name() + ">";

                    types.append(argValue);
                }

                builder.setMessage(format.replaceAll("\\{args}", String.valueOf(types)));

                String hoverShit = baseFormat.append(types).toString();

                String hoverFormat = CrazyCore.api().commandHoverFormat();

                builder.hover(PlaceholderSupport.setPlaceholders(hoverFormat).replaceAll("\\{commands}", hoverShit)).click(hoverShit, ClickEvent.Action.valueOf(CrazyCore.api().commandHoverAction().toUpperCase()));

                actor.reply(builder.build());
            }

            String footer = CrazyCore.api().commandHelpFooter();

            if (actor.isPlayer()) {
                String text = CrazyCore.api().commandNavigationText();

                if (page > 1) {
                    int number = page-1;

                    hover(actor.getPlayer(), footer.replaceAll("\\{page}", String.valueOf(page)),  text.replaceAll("\\{page}", String.valueOf(number)), CrazyCore.api().commandNavigationBackButton(), "/crazycrates help " + number, ClickEvent.Action.RUN_COMMAND);
                } else if (page < this.manager.getClasses().size()) {
                    int number = page+1;

                    hover(actor.getPlayer(), footer.replaceAll("\\{page}", String.valueOf(page)),  text.replaceAll("\\{page}", String.valueOf(number)), CrazyCore.api().commandNavigationNextButton(), "/crazycrates help " + number, ClickEvent.Action.RUN_COMMAND);
                }
            } else {
                send(actor.getSender(), footer.replaceAll("\\{page}", String.valueOf(page)), false, "");
            }
        }

        this.lastPage = max >= this.totalResults;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public void setPage(int page, int perPage) {
        this.setPage(page);
        this.setPerPage(perPage);
    }

    public int getPage() {
        return this.page;
    }

    public int getPerPage() {
        return this.perPage;
    }

    public int getTotalResults() {
        return this.totalResults;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public boolean isLastPage() {
        return this.lastPage;
    }
}