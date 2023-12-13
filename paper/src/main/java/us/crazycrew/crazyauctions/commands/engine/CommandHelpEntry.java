package us.crazycrew.crazyauctions.commands.engine;

import com.badbones69.crazyauctions.common.AuctionsFactory;
import com.badbones69.crazyauctions.common.config.types.MessageKeys;
import com.badbones69.crazyauctions.common.contexts.builder.ComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.CrazyAuctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandHelpEntry {

    @NotNull
    private final CrazyAuctions plugin = CrazyAuctions.get();

    private int page = 1;
    private int perPage;
    private int totalPages;
    private int totalResults;
    private boolean lastPage;

    public CommandHelpEntry() {
        this.perPage = 10;
    }

    public void help(CommandContext context) {
        int min = this.perPage * (this.page - 1);
        int max = min + this.perPage;

        Map<String, CommandEngine> commands = this.plugin.getCommandManager().getCommands();

        this.totalResults = commands.size();

        this.totalPages = this.totalResults / this.perPage;

        if (min >= this.totalResults) {
            context.reply("Invalid page");
            return;
        }

        Component footerComponent = null;

        String header = AuctionsFactory.getMessages().getProperty(MessageKeys.page_header);

        context.reply(header.replaceAll("\\{page}", String.valueOf(this.page)));

        String footer = AuctionsFactory.getMessages().getProperty(MessageKeys.page_footer);

        for (int value = min; value < max && value < this.totalResults; value++) {
            if (this.totalResults - 1 < value) continue;

            CommandEngine command = this.plugin.getCommandManager().getClasses().get(value);

            String label = command.getLabel();
            String description = command.getDescription();
            String permission = command.getPermission();

            if (context.isPlayer()) {
                if (!context.getPlayer().hasPermission(permission)) {
                    // does not have permission so we do nothing.
                    return;
                }
            }

            StringBuilder root = new StringBuilder("/" + command.getCommand().getName());

            root.append(" ").append(label);

            String format = AuctionsFactory.getMessages().getProperty(MessageKeys.page_format)
                    .replaceAll("\\{command}", root.toString())
                    .replaceAll("\\{description}", description);

            if (context.isPlayer()) {
                ComponentBuilder commandBuilder = new ComponentBuilder();

                commandBuilder.setMessage(format);

                String hoverFormat = AuctionsFactory.getMessages().getProperty(MessageKeys.hover_format);

                commandBuilder.hover(hoverFormat.replaceAll("\\{command}", root.toString()))
                        .click(ClickEvent.Action.COPY_TO_CLIPBOARD, root.toString());

                context.reply(commandBuilder.build());

                ComponentBuilder builder = new ComponentBuilder();

                if (this.page > 1) {
                    int number = this.page-1;

                    String usage = "/" + command.getCommand().getName() + " help " + number;
                    String newPage = String.valueOf(this.page);

                    builder.setMessage(footer.replaceAll("\\{page}", newPage));

                    builder.getFancyComponentBuilder().hover(AuctionsFactory.getMessages().getProperty(MessageKeys.back_button), AuctionsFactory.getMessages().getProperty(MessageKeys.navigation_text).replaceAll("\\{type}", "back"));

                    builder.getFancyComponentBuilder().click(ClickEvent.Action.RUN_COMMAND, usage);
                } else if (this.page < this.plugin.getCommandManager().getClasses().size()) {
                    int number = this.page+1;

                    String usage = "/" + command.getCommand().getName() + " help " + number;
                    String newPage = String.valueOf(this.page);

                    builder.setMessage(footer.replaceAll("\\{page}", newPage));

                    builder.getFancyComponentBuilder().hover(AuctionsFactory.getMessages().getProperty(MessageKeys.next_button), AuctionsFactory.getMessages().getProperty(MessageKeys.navigation_text).replaceAll("\\{type}", "next"));

                    builder.getFancyComponentBuilder().click(ClickEvent.Action.RUN_COMMAND, usage);
                }

                footerComponent = builder.build();
            }
        }

        if (context.isPlayer()) {
            context.reply(footerComponent);
        } else {
            context.reply(footer.replaceAll("\\{page}", String.valueOf(this.page)));
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
        this.setPerPage(perPage);
        this.setPage(page);
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