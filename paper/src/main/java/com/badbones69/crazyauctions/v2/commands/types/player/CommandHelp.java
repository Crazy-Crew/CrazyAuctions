package com.badbones69.crazyauctions.v2.commands.types.player;

import com.badbones69.crazyauctions.v2.commands.types.BaseCommand;
import com.badbones69.crazyauctions.v2.api.gui.AuctionGui;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandHelp extends BaseCommand {

    @Command
    @Permission(value = "crazyauctions.access", def = PermissionDefault.TRUE)
    @Syntax("/crazyauctions")
    public void root(final Player player) {
        final YamlCustomFile customFile = this.fileManager.getConfigurateFile(this.path.resolve("guis").resolve("auction.yml"));

        if (customFile == null) {
            this.fusion.log("warn", "auctions.yml is not in the cache.");

            return;
        }

        new AuctionGui(customFile).open(player);
    }

    @Command(value = "help")
    @Permission(value = "crazyauctions.help", def = PermissionDefault.OP)
    @Syntax("/crazyauctions help")
    public void help(final CommandSender sender) {

    }
}