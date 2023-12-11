package us.crazycrew.crazyauctions.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.CrazyAuctions;

@Command("ah")
public class AuctionCommand {

    @NotNull
    private final CrazyAuctions plugin = CrazyAuctions.get();

    @Default
    public static void auction(Player player) {
        // Open the main menu.
    }

    @Subcommand("help")
    @Permission("crazyauctions.help")
    public static void help(CommandSender sender) {
        // Send the help message.
    }

    @Subcommand("reload")
    @Permission("crazyauctions.reload")
    public static void reload(CommandSender sender) {
        // Reload the plugin.
    }
}