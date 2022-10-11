package com.badbones69.crazyauctions.api.economy;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.economy.vault.VaultSupport;
import com.badbones69.crazyauctions.api.enums.ShopCategories;
import org.bukkit.entity.Player;

public class CurrencyAPI {

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();

    private final VaultSupport vaultSupport = plugin.getStarter().getVaultSupport();
    
    /**
     * Get the amount that a player has from a specific currency.
     * @param player The player you wish to get the amount from.
     * @param currency The currency you wish to get from.
     * @return The amount that the player has of that currency.
     */
    public int getCurrency(Player player, Currency currency) {
        try {
            switch (currency) {
                case VAULT:
                    vaultSupport.getVault().getBalance(player);
                    break;
                case XP_LEVEL:
                    player.getLevel();
                    break;
                case XP_TOTAL:
                    getTotalExperience(player);
                    break;
            }
        } catch (Exception | NoClassDefFoundError ignored) {}
        return 0;
    }
    
    /**
     * Take an amount from a player's currency.
     * @param player The player you wish to take from.
     * @param option The ShopOption you wish to use.
     */
    public void takeCurrency(Player player, ShopCategories option) {
        // takeCurrency(player, option.getCurrency(), option.getCost());
    }
    
    /**
     * Take an amount from a player's currency.
     * @param player The player you wish to take from.
     * @param currency The currency you wish to use.
     * @param amount The amount you want to take.
     */
    public void takeCurrency(Player player, Currency currency, int amount) {
        try {
            switch (currency) {
                case VAULT:
                    vaultSupport.getVault().withdrawPlayer(player, amount);
                    break;
                case XP_LEVEL:
                    player.setLevel(player.getLevel() - amount);
                    break;
                case XP_TOTAL:
                    takeTotalExperience(player, amount);
                    break;
            }
        } catch (Exception | NoClassDefFoundError ignored) {}
    }
    
    /**
     * Give an amount to a player's currency.
     * @param player The player you are giving to.
     * @param currency The currency you want to use.
     * @param amount The amount you are giving to the player.
     */
    public void giveCurrency(Player player, Currency currency, int amount) {
        try {
            switch (currency) {
                case VAULT:
                    vaultSupport.getVault().depositPlayer(player, amount);
                    break;
                case XP_LEVEL:
                    player.setLevel(player.getLevel() + amount);
                    break;
                case XP_TOTAL:
                    takeTotalExperience(player, -amount);
                    break;
            }
        } catch (Exception | NoClassDefFoundError ignored) {}
    }
    
    /**
     * Checks if the player has enough of a currency.
     * @param player The player you are checking.
     * @param option The ShopOption you wish to check.
     * @return True if they have enough to buy it or false if they don't.
     */
    public boolean canBuy(Player player, ShopCategories option) {
        return canBuy(player, option.getCurrency(), option.getCost());
    }
    
    /**
     * Checks if the player has enough of a currency.
     * @param player The player you are checking.
     * @param currency The currency you wish to check.
     * @param cost The cost of the item you are checking.
     * @return True if they have enough to buy it or false if they don't.
     */
    public boolean canBuy(Player player, Currency currency, int cost) {
        return getCurrency(player, currency) >= cost;
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
    
    /**
     * Loads the vault currency if it is on the server.
     */
    public void loadCurrency() {
        vaultSupport.loadVault();
    }
}