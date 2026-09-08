package com.badbones69.crazyauctions.currency;

import com.badbones69.crazyauctions.CrazyAuctions;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

public class VaultSupport {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final Server server = this.plugin.getServer();

    private final ServicesManager servicesManager = this.server.getServicesManager();

    private Economy vault = null;

    public boolean setupEconomy() {
        final RegisteredServiceProvider<Economy> serviceProvider = this.servicesManager.getRegistration(Economy.class);

        if (serviceProvider != null) {
            this.vault = serviceProvider.getProvider();
        }

        return this.vault != null;
    }

    public double getMoney(@NotNull final OfflinePlayer player) {
        if (this.vault == null) {
            return 0.0;
        }

        return this.vault.getBalance(player);
    }

    public boolean removeMoney(@NotNull final Player player, final double amount) {
        if (this.vault == null) {
            return false;
        }

        final EconomyResponse result = this.vault.withdrawPlayer(player, amount);

        return result.transactionSuccess();
    }

    public boolean removeMoney(@NotNull final OfflinePlayer player, final double amount) {
        if (this.vault == null) {
            return false;
        }

        final EconomyResponse result = this.vault.withdrawPlayer(player, amount);

        return result.transactionSuccess();
    }

    public boolean addMoney(final Player player, final double amount) {
        if (this.vault == null) {
            return false;
        }

        final EconomyResponse result = this.vault.depositPlayer(player, amount);

        return result.transactionSuccess();
    }

    public boolean addMoney(final OfflinePlayer player, final double amount) {
        if (this.vault == null) {
            return false;
        }

        final EconomyResponse result = this.vault.depositPlayer(player, amount);

        return result.transactionSuccess();
    }
}