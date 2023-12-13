package us.crazycrew.crazyauctions.commands.engine;

import com.badbones69.crazyauctions.common.contexts.PlayerContext;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandContext extends PlayerContext {

    private final CommandSender sender;
    private final CommandArguments args;

    public CommandContext(CommandSender sender, CommandArguments args) {
        super(sender);

        this.sender = sender;
        this.args = args;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public Player getPlayer() {
        return (Player) this.sender;
    }

    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    public CommandArguments getArgs() {
        return this.args;
    }
}