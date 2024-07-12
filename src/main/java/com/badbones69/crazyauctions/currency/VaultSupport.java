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
        return this.vault;
    }

    public void loadVault() {
        RegisteredServiceProvider<Economy> serviceProvider = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (serviceProvider != null) this.vault = serviceProvider.getProvider();
    }

    public long getMoney(@NotNull Player player) {
        return (long) this.vault.getBalance(player);
    }

    public void removeMoney(@NotNull Player player, long amount) {
        this.vault.withdrawPlayer(player, amount);
    }

    public void removeMoney(@NotNull OfflinePlayer player, long amount) {
        this.vault.withdrawPlayer(player, amount);
    }

    public void addMoney(Player player, long amount) {
        this.vault.depositPlayer(player, amount);
    }

    public void addMoney(OfflinePlayer player, long amount) {
        this.vault.depositPlayer(player, amount);
    }
}