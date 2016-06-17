package me.BadBones69.CrazyAuctions.Currency;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.BadBones69.CrazyAuctions.Main;

public enum CM { // Currency Manager
	VAULT("Vault", "Money");

	String PluginName, Name;

	/**
	 * @param pluginname
	 *            Name of the Plugin.
	 * @param name
	 *            Name of the Currency.
	 */
	private CM(String pluginname, String name) {
		this.PluginName = pluginname;
		this.Name = name;
	}

	/**
	 * @return Returns the Currency name as a string.
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @return Returns the Currency name as a string.
	 */
	public String getPluginName() {
		return PluginName;
	}

	/**
	 * @param name
	 *            Name of the Type you want.
	 * @return Returns the Currency as a Enum.
	 */
	public static CM getFromName(String name) {
		for (CM type : CM.values()) {
			if (type.getPluginName().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return Returns true if the server has the plugin.
	 */
	public Boolean hasPlugin() {
		if (Bukkit.getServer().getPluginManager().getPlugin(PluginName) != null) {
			if (Main.settings.getConfig().getBoolean("Settings.Currencies." + PluginName + ".Enabled")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param player
	 *            Player you want the currency from.
	 * @param type
	 *            Type of currency you want to get.
	 * @return Returns the amount they have of the currency
	 */
	public static Long getMoney(Player player) {
		return Vault.getMoney(player);
	}

	/**
	 * 
	 * @param player
	 *            Player you want the currency from.
	 * @param type
	 *            Type of currency you want to take from.
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
	 * @param type
	 *            Type of currency you want to take from.
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
	 * @param type
	 *            Type of currency you want to add to.
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
	 * @param type
	 *            Type of currency you want to add to.
	 * @param amount
	 *            The amount you want to add.
	 */
	public static void addMoney(OfflinePlayer player, Long amount) {
		Vault.addMoney(player, amount);
	}
}