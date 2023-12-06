package com.badbones69.crazyauctions.currency;

import com.badbones69.crazyauctions.CrazyAuctions;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultSupport {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private Economy vault = null;

    public Economy getVault() {
        return vault;
    }

    public void loadVault() {
        RegisteredServiceProvider<Economy> serviceProvider = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (serviceProvider != null) vault = serviceProvider.getProvider();
    }

    public Long getMoney(@NotNull Player player) {
        return (long) vault.getBalance(player);
    }

    public void removeMoney(@NotNull Player player, Long amount) {
        vault.withdrawPlayer(player, amount);
    }

    public void removeMoney(@NotNull OfflinePlayer player, Long amount) {
        vault.withdrawPlayer(player, amount);
    }

    public void addMoney(Player player, Long amount) {
        vault.depositPlayer(player, amount);
    }

    public void addMoney(OfflinePlayer player, Long amount) {
        vault.depositPlayer(player, amount);
    }
}