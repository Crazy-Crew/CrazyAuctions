package me.badbones69.crazyauctions.currency;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Vault {
	public static Economy econ = null;
	public static EconomyResponse r;
	public static boolean hasVault(){
        if(Bukkit.getServer().getPluginManager().getPlugin("Vault")!=null){
            return true;
        }
        return false;
    }
	public static boolean setupEconomy(){
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null){
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	public static Long getMoney(Player player){
		return (long) econ.getBalance(player);
	}
	public static void removeMoney(Player player, Long amount){
		econ.withdrawPlayer(player, amount);
	}
	public static void removeMoney(OfflinePlayer player, Long amount){
		econ.withdrawPlayer(player, amount);
	}
	public static void addMoney(Player player, Long amount){
		econ.depositPlayer(player, amount);
	}
	public static void addMoney(OfflinePlayer player, Long amount){
		econ.depositPlayer(player, amount);
	}
}