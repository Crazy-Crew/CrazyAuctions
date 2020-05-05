package me.badbones69.crazyauctions.commands;

import me.badbones69.crazyauctions.api.managers.AuctionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CACommand implements CommandExecutor {
    
    private static AuctionManager auctionManager = AuctionManager.getInstance();
    
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
            
            }
        } else {
        
        }
        return false;
    }
    
    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("crazyauctions." + node) || sender.hasPermission("crazyauctions.admin");
    }
    
}
