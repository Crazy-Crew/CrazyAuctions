package com.badbones69.crazyauctions.commands.features.player;

import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyauctions.controllers.GuiListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandExpired extends BaseCommand {

    @Command(value = "expired", alias = "collect")
    @Permission(value = "crazyauctions.expired", def = PermissionDefault.TRUE)
    @Syntax("/crazyauctions expired/collect")
    public void expired(final Player player) {
        GuiListener.openPlayersExpiredList(player, 1);
    }
}