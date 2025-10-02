package com.badbones69.crazyauctions.currency;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.enums.CurrencyData;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CoinsEngineSupport {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    public double getMoney(@NotNull Player player, @NotNull String currencyName) {
        return CoinsEngineAPI.getBalance(player.getUniqueId(), currencyName);
    }

    public double getMoney(@NotNull OfflinePlayer player, @NotNull String currencyName) {
        return CoinsEngineAPI.getBalance(player.getUniqueId(), currencyName);
    }

    public boolean removeMoney(@NotNull Player player, double amount, @NotNull String currencyName) {
        return CoinsEngineAPI.removeBalance(player.getUniqueId(), currencyName, amount);
    }

    public boolean removeMoney(@NotNull OfflinePlayer player, double amount, @NotNull String currencyName) {
        return CoinsEngineAPI.removeBalance(player.getUniqueId(), currencyName, amount);
    }

    public boolean addMoney(@NotNull Player player, double amount, @NotNull String currencyName) {
        return CoinsEngineAPI.addBalance(player.getUniqueId(), currencyName, amount);
    }

    public boolean addMoney(@NotNull OfflinePlayer player, double amount, @NotNull String currencyName) {
        return CoinsEngineAPI.addBalance(player.getUniqueId(), currencyName, amount);
    }

    public static CurrencyData getCurrency(String currencyName) {
        Currency currency = CoinsEngineAPI.getCurrency(currencyName);
        if (currency == null) return null;

        return new CurrencyData(
            currency.getId(),
            currency.getName(),
            currency.getSymbol(),
            currency.getIcon()
        );
    }
}
