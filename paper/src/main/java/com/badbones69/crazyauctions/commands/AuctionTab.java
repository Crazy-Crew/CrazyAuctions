package com.badbones69.crazyauctions.commands;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuctionTab implements TabCompleter {

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) { // /voucher
            if (hasPermission(sender, "access")) {
                completions.add("help");
                completions.add("collect");
                completions.add("expired");
                completions.add("listed");
            }

            if (hasPermission(sender, "test")) completions.add("test");
            if (hasPermission(sender, "admin")) completions.add("reload");
            if (hasPermission(sender, "view")) completions.add("view");
            if (hasPermission(sender, "sell")) completions.add("sell");
            if (hasPermission(sender, "bid")) completions.add("bid");
            if (hasPermission(sender, "migrate")) completions.add("migrate");

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        } else if (args.length == 2) { // /crazyauctions arg0
            switch (args[0].toLowerCase()) {
                case "bid", "sell" -> completions.addAll(Arrays.asList("50", "100", "250", "500", "1000", "2500", "5000", "10000"));
                case "view" -> completions.addAll(this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());
            }

            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        } else if (args.length == 3) { // /crazyauctions arg0 arg1
            switch (args[0].toLowerCase()) {
                case "bid", "sell" -> completions.addAll(Arrays.asList("1", "2", "4", "8", "10", "20", "40", "64"));
            }

            return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
        }

        return new ArrayList<>();
    }
    
    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("crazyauctions." + node) || sender.hasPermission("crazyauctions.admin.*");
    }
}