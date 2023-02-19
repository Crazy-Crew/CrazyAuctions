package com.badbones69.crazyauctions.commands;

import com.badbones69.crazyauctions.configs.data.AuctionData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class AuctionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        AuctionData.auctionData.putIfAbsent(player.getUniqueId(), UUID.randomUUID().toString());

        AuctionData.save();

        return false;
    }
}