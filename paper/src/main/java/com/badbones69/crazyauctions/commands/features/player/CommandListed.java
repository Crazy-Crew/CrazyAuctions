package com.badbones69.crazyauctions.commands.features.player;

import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyauctions.controllers.GuiListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandListed extends BaseCommand {

    @Command(value = "listed")
    @Permission(value = "crazyauctions.listed", def = PermissionDefault.TRUE)
    @Syntax("/crazyauctions listed")
    public void listed(final Player player) {
        GuiListener.openPlayersCurrentList(player, 1);
    }
}