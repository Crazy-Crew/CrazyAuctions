package us.crazycrew.crazyauctions.commands;

import com.badbones69.crazyauctions.common.AuctionsFactory;
import com.badbones69.crazyauctions.common.config.types.AuctionKeys;
import com.ryderbelserion.cluster.utils.AdvUtils;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.CrazyAuctions;
import us.crazycrew.crazyauctions.commands.subs.HelpCommand;
import us.crazycrew.crazyauctions.commands.subs.ReloadCommand;
import us.crazycrew.crazyauctions.menus.AuctionHouseMenu;

public class CommandManager {

    @NotNull
    private final CrazyAuctions plugin = CrazyAuctions.get();

    public void load() {
        // Create command config.
        CommandAPIBukkitConfig config = new CommandAPIBukkitConfig(this.plugin);
        config.shouldHookPaperReload(true);

        // Load command api.
        CommandAPI.onLoad(config);
    }

    public void enable() {
        // Enable command api.
        CommandAPI.onEnable();

        // Create default command.
        CommandAPICommand command = new CommandAPICommand("ah")
                .withPermission("crazyauctions.help")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(AdvUtils.parse("<red>Must be a player."));
                        return;
                    }

                    AuctionHouseMenu auctions = new AuctionHouseMenu(player, 54, AuctionsFactory.getAuctions().getProperty(AuctionKeys.inventory_name));
                    auctions.open();
                });

        // Bind subcommand to the object above.
        new ReloadCommand(command).registerSubCommand();
        new HelpCommand(command).registerSubCommand();

        // Register it all.
        command.register();
    }
}