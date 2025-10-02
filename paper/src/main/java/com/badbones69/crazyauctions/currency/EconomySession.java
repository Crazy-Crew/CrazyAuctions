package com.badbones69.crazyauctions.currency;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public interface EconomySession {
    double getMoney(Player player);
    double getMoney(OfflinePlayer player);
    boolean removeMoney(Player player, double amount);
    boolean removeMoney(OfflinePlayer player, double amount);
    boolean addMoney(Player player, double amount);
    boolean addMoney(OfflinePlayer player, double amount);
}
