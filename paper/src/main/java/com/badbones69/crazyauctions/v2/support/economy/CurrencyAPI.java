package com.badbones69.crazyauctions.support.economy;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.UUID;

/**
 * Description: Defines what currency to use.
 */
public class CurrencyAPI {

    // TODO() Add item support as a currency.

    private final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    /**
     * Get the amount that a player has from a specific currency.
     *
     * @param uuid - The uuid of the player.
     * @param currency - The currency you wish to get from.
     * @return amount that the player has of that currency.
     */
    public int getCurrency(UUID uuid, Currency currency) {
        try {
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(uuid);
            Player player = plugin.getServer().getPlayer(uuid);

            switch (currency) {
                case VAULT: //if (player != null) return (int) plugin.getVaultSupport().getVault().getBalance(player);

                case XP_LEVEL: if (player != null) return player.getLevel();

                case XP_TOTAL: if (player != null) return getTotalExperience(player);
            }
        } catch (Exception | NoClassDefFoundError ignored) {}
        return 0;
    }
    
    /**
     * Take an amount from a player's currency.
     *
     * @param uuid - The uuid of the player.
     * @param currency - The currency you wish to use.
     * @param amount - The amount you want to take.
     */
    public void takeCurrency(UUID uuid, Currency currency, int amount) {
        try {
            Player player = plugin.getServer().getPlayer(uuid);

            switch (currency) {
                //case VAULT: if (player != null) plugin.getVaultSupport().getVault().withdrawPlayer(player, amount);
                case XP_LEVEL: if (player != null) player.setLevel(player.getLevel() - amount);
                case XP_TOTAL: if (player != null) takeTotalExperience(player, amount);
            }
        } catch (Exception | NoClassDefFoundError ignored) {}
    }
    
    /**
     * Give an amount to a player's currency.
     *
     * @param uuid - The uuid of the player.
     * @param currency - The currency you want to use.
     * @param amount - The amount you are giving to the player.
     */
    public void giveCurrency(UUID uuid, Currency currency, int amount) {
        try {
            Player player = plugin.getServer().getPlayer(uuid);

            switch (currency) {
                //case VAULT: if (player != null) plugin.getVaultSupport().getVault().depositPlayer(player, amount);
                case XP_LEVEL: if (player != null) player.setLevel(player.getLevel() + amount);
                case XP_TOTAL: if (player != null) takeTotalExperience(player, -amount);
            }
        } catch (Exception | NoClassDefFoundError ignored) {}
    }
    
    /**
     * Checks if the player has enough of a currency.
     *
     * @param uuid - The uuid of the player.
     * @param currency The currency you wish to check.
     * @param cost The cost of the item you are checking.
     * @return true if they have enough to buy it or false if they don't.
     */
    public boolean canBuy(UUID uuid, Currency currency, int cost) {
        return getCurrency(uuid, currency) >= cost;
    }
    
    private void takeTotalExperience(Player player, int amount) {
        int total = getTotalExperience(player) - amount;
        player.setTotalExperience(0);
        player.setTotalExperience(total);
        player.setLevel(0);
        player.setExp(0);

        while (total > player.getExpToLevel()) {
            total -= player.getExpToLevel();
            player.setLevel(player.getLevel() + 1);
        }

        float xp = (float) total / (float) player.getExpToLevel();
        player.setExp(xp);
    }
    
    private int getTotalExperience(Player player) { // https://www.spigotmc.org/threads/72804
        int experience;
        int level = player.getLevel();

        if (level >= 0 && level <= 15) {
            experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
            int requiredExperience = 2 * level + 7;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += Math.ceil(currentExp * requiredExperience);
            return experience;
        } else if (level > 15 && level <= 30) {
            experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
            int requiredExperience = 5 * level - 38;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += Math.ceil(currentExp * requiredExperience);
            return experience;
        } else {
            experience = (int) Math.ceil((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220));
            int requiredExperience = 9 * level - 158;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += Math.ceil(currentExp * requiredExperience);
            return experience;
        }
    }
}