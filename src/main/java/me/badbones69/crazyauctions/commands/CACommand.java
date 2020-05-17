package me.badbones69.crazyauctions.commands;

import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.api.CrazyAuctions;
import me.badbones69.crazyauctions.api.FileManager;
import me.badbones69.crazyauctions.api.enums.Messages;
import me.badbones69.crazyauctions.api.managers.AuctionManager;
import me.badbones69.crazyauctions.api.objects.items.SellItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CACommand implements CommandExecutor {
    
    private static FileManager fileManager = FileManager.getInstance();
    private static CrazyAuctions crazyAuctions = CrazyAuctions.getInstance();
    private static AuctionManager auctionManager = AuctionManager.getInstance();
    
    //TODO Auction house object is currently not updating when item is added to it.
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isPlayer = sender instanceof Player;
        Player player = isPlayer ? (Player) sender : null;
        if (args.length == 0) {
            if (isPlayer) {
                if (hasPermission(sender, "access")) {
                    auctionManager.openAuctionHouse(player);
                }
            } else {
                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            }
            return true;
        } else {
            switch (args[0].toLowerCase()) {
                case "help":
                    sender.sendMessage(Messages.HELP.getMessage());
                    return true;
                case "reload":
                    fileManager.setup(crazyAuctions.getPlugin());
                    return true;
                case "test":
                    if (isPlayer) {
                        long price = 10;
                        int amount = 1;
                        ItemStack item = Methods.getItemInHand(player);
                        if (args.length >= 2) {
                            if (!Methods.isInt(args[1])) {
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Arg%", args[1]);
                                placeholders.put("%arg%", args[1]);
                                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage(placeholders));
                                return true;
                            }
                            amount = Integer.parseInt(args[1]);
                        }
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer("Test-Player");
                        for (int i = 1; amount > i; i++) {
                            auctionManager.addAuctionItem(new SellItem(player, item, price));
                        }
                    } else {
                        sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                    }
                    return true;
            }
        }
        return false;
    }
    
    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("crazyauctions." + node) || sender.hasPermission("crazyauctions.admin");
    }
    
}
