package me.badbones69.crazyauctions.currency;

import me.badbones69.crazyauctions.api.FileManager.Files;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public enum CurrencyManager { // Currency Manager
    
    VAULT("Vault", "Money");
    
    private String pluginName, name;
    
    /**
     * @param pluginname
     *            name of the Plugin.
     * @param name
     *            name of the Currency.
     */
    private CurrencyManager(String pluginname, String name) {
        this.pluginName = pluginname;
        this.name = name;
    }
    
    /**
     * @param name
     *            name of the Type you want.
     * @return Returns the Currency as a Enum.
     */
    public static CurrencyManager getFromName(String name) {
        for (CurrencyManager type : CurrencyManager.values()) {
            if (type.getPluginName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     *
     * @param player
     *            Player you want the currency from.
     * @return Returns the amount they have of the currency
     */
    public static Long getMoney(Player player) {
        return Vault.getMoney(player);
    }
    
    /**
     *
     * @param player
     *            Player you want the currency from.
     * @param amount
     *            The amount you want to take.
     */
    public static void removeMoney(Player player, Long amount) {
        Vault.removeMoney(player, amount);
    }
    
    /**
     *
     * @param player
     *            Player you want the currency from.
     * @param amount
     *            The amount you want to take.
     */
    public static void removeMoney(OfflinePlayer player, Long amount) {
        Vault.removeMoney(player, amount);
    }
    
    /**
     *
     * @param player
     *            Player you want the currency from.
     * @param amount
     *            The amount you want to add.
     */
    public static void addMoney(Player player, Long amount) {
        Vault.addMoney(player, amount);
    }
    
    /**
     *
     * @param player
     *            Player you want the currency from.
     * @param amount
     *            The amount you want to add.
     */
    public static void addMoney(OfflinePlayer player, Long amount) {
        Vault.addMoney(player, amount);
    }
    
    /**
     * @return Returns the Currency name as a string.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return Returns the Currency name as a string.
     */
    public String getPluginName() {
        return pluginName;
    }
    
    /**
     *
     * @return Returns true if the server has the plugin.
     */
    public Boolean hasPlugin() {
        if (Bukkit.getServer().getPluginManager().getPlugin(pluginName) != null) {
            return Files.CONFIG.getFile().getBoolean("Settings.Currencies." + pluginName + ".Enabled");
        }
        return false;
    }
    
}