package com.badbones69.crazyauctions.currency;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class EconomySessionFactory {

    public static EconomySession create(Object support, String currency) {
        if (support instanceof CoinsEngineSupport coinsSupport) {
            return new EconomySession() {
                @Override
                public double getMoney(Player player) {
                    return coinsSupport.getMoney(player, currency);
                }

                @Override
                public boolean removeMoney(Player player, double amount) {
                    return coinsSupport.removeMoney(player, amount, currency);
                }

                @Override
                public boolean removeMoney(OfflinePlayer player, double amount) {
                    return coinsSupport.removeMoney(player, amount, currency);
                }

                @Override
                public boolean addMoney(Player player, double amount) {
                    return coinsSupport.addMoney(player, amount, currency);
                }

                @Override
                public boolean addMoney(OfflinePlayer player, double amount) {
                    return coinsSupport.addMoney(player, amount, currency);
                }
            };
        } else if (support instanceof VaultSupport vaultSupport) {
            return new EconomySession() {
                @Override
                public double getMoney(Player player) {
                    return vaultSupport.getMoney(player);
                }

                @Override
                public boolean removeMoney(Player player, double amount) {
                    return vaultSupport.removeMoney(player, (long) amount);
                }

                @Override
                public boolean removeMoney(OfflinePlayer player, double amount) {
                    return vaultSupport.removeMoney(player, (long) amount);
                }

                @Override
                public boolean addMoney(Player player, double amount) {
                    return vaultSupport.addMoney(player, (long) amount);
                }

                @Override
                public boolean addMoney(OfflinePlayer player, double amount) {
                    return vaultSupport.addMoney(player, (long) amount);
                }
            };
        }

        throw new IllegalArgumentException("Unsupported economy support: " + support.getClass().getName());
    }

    public static EconomySession create(Object support) {
        return create(support, null);
    }
}
