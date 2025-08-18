package com.badbones69.crazyauctions.commands.features.player;

import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.ryderbelserion.fusion.paper.api.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.*;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.UUID;

public class CommandView extends BaseCommand {

    @Command(value = "view")
    @Permission(value = "crazyauctions.view", def = PermissionDefault.TRUE)
    @Syntax("/crazyauctions view <player>")
    public void view(final Player sender, @Optional @ArgName("player") @Suggestion("players") final PlayerBuilder target) {
        if (target == null) { //todo() temporarily until I get what I want done.
            view(sender);

            return;
        }

        final UUID uuid = target.getUniqueId();

        if (uuid == null) {
            //todo() add debugging

            return;
        }

        GuiListener.openViewer(sender, uuid, 1);
    }
}