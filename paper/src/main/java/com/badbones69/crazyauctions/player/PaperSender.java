package com.badbones69.crazyauctions.player;

import com.ryderbelserion.ithildin.core.IthildinCore;
import com.ryderbelserion.ithildin.core.Sender;
import com.ryderbelserion.ithildin.core.registry.keys.Key;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PaperSender extends Sender {

    public PaperSender(@NotNull Key key) {
        super(key);
    }

    public static Sender getSender(CommandSender sender) {
        if (sender instanceof Player player) return IthildinCore.api().getPlayerRegistry().get(player.getUniqueId());

        return IthildinCore.api().getConsole();
    }

    public static CommandSender getSender(Sender sender) {
        return Bukkit.getConsoleSender();
    }
}