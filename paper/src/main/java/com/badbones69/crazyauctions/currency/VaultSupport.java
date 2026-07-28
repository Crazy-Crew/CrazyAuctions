package com.badbones69.crazyauctions.currency;

import com.badbones69.crazyauctions.CrazyAuctions;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultSupport {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private Economy vault = null;
    private net.milkbowl.vault.permission.Permission permissions = null;

    public Economy getVault() {
        return this.vault;
    }

    public boolean setupEconomy() {
        RegisteredServiceProvider<Economy> serviceProvider = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (serviceProvider != null) this.vault = serviceProvider.getProvider();

        return this.vault != null;
    }

    public void setupPermissions() {
        final RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> serviceProvider = this.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);

        if (serviceProvider != null) this.permissions = serviceProvider.getProvider();
    }

    public boolean hasPermission(@NotNull OfflinePlayer player, @NotNull String permission) {
        final Player onlinePlayer = player.getPlayer();

        if (onlinePlayer != null) return onlinePlayer.hasPermission(permission);

        if (player.getName() == null) return false;

        return this.permissions != null && this.permissions.playerHas((String) null, player, permission);
    }

    public double getMoney(@NotNull OfflinePlayer player) {
        return this.vault.getBalance(player);
    }

    public boolean removeMoney(@NotNull Player player, double amount) {
        EconomyResponse result = this.vault.withdrawPlayer(player, amount);
        return result.transactionSuccess();
    }

    public boolean removeMoney(@NotNull OfflinePlayer player, double amount) {
        EconomyResponse result = this.vault.withdrawPlayer(player, amount);
        return result.transactionSuccess();
    }

    public boolean addMoney(Player player, double amount) {
        EconomyResponse result = this.vault.depositPlayer(player, amount);
        return result.transactionSuccess();
    }

    public boolean addMoney(OfflinePlayer player, double amount) {
        EconomyResponse result = this.vault.depositPlayer(player, amount);
        return result.transactionSuccess();
    }
}
