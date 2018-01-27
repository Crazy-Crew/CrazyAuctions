package me.badbones69.crazyauctions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.badbones69.crazyauctions.currency.CurrencyManager;
import me.badbones69.crazyauctions.events.AuctionWinBidEvent;

public class Methods {
	
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyAuctions");
	
	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static String removeColor(String msg) {
		return ChatColor.stripColor(msg);
	}
	
	public static String getPrefix() {
		return color(Main.settings.getConfig().getString("Settings.Prefix"));
	}
	
	public static ItemStack makeItem(String type, int amount) {
		int ty = 0;
		if(type.contains(":")) {
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		ItemStack item = null;
		try {
			item = new ItemStack(m, amount, (short) ty);
		}catch(Exception e) {
			item = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
		}
		return item;
	}
	
	public static ItemStack makeItem(String type, int amount, String name) {
		int ty = 0;
		if(type.contains(":")) {
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		ItemStack item = null;
		try {
			item = new ItemStack(m, amount, (short) ty);
		}catch(Exception e) {
			item = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
		}
		ItemMeta me = item.getItemMeta();
		me.setDisplayName(color(name));
		item.setItemMeta(me);
		return item;
	}
	
	public static ItemStack makeItem(String type, int amount, String name, List<String> lore) {
		ArrayList<String> l = new ArrayList<String>();
		int ty = 0;
		if(type.contains(":")) {
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		ItemStack item = null;
		try {
			item = new ItemStack(m, amount, (short) ty);
		}catch(Exception e) {
			item = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
		}
		ItemMeta me = item.getItemMeta();
		me.setDisplayName(color(name));
		for(String L : lore)
			l.add(color(L));
		me.setLore(l);
		item.setItemMeta(me);
		return item;
	}
	
	public static ItemStack makeItem(Material material, int amount, int type, String name) {
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		item.setItemMeta(m);
		return item;
	}
	
	public static ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore) {
		ArrayList<String> l = new ArrayList<String>();
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		for(String L : lore)
			l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		return item;
	}
	
	public static ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore, Map<Enchantment, Integer> enchants) {
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(name);
		m.setLore(lore);
		item.setItemMeta(m);
		item.addUnsafeEnchantments(enchants);
		return item;
	}
	
	public static ItemStack addLore(ItemStack item, String i) {
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta m = item.getItemMeta();
		if(item.getItemMeta().hasLore()) {
			lore.addAll(item.getItemMeta().getLore());
		}
		lore.add(i);
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	
	public static ItemStack addLore(ItemStack item, List<String> list) {
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta m = item.getItemMeta();
		if(item.getItemMeta().hasLore()) lore.addAll(item.getItemMeta().getLore());
		for(String i : list)
			lore.add(color(i));
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	
	public static Integer getVersion() {
		String ver = Bukkit.getServer().getClass().getPackage().getName();
		ver = ver.substring(ver.lastIndexOf('.') + 1);
		ver = ver.replaceAll("_", "").replaceAll("R", "").replaceAll("v", "");
		return Integer.parseInt(ver);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player) {
		if(getVersion() >= 191) {
			return player.getInventory().getItemInMainHand();
		}else {
			return player.getItemInHand();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void setItemInHand(Player player, ItemStack item) {
		if(Methods.getVersion() >= 191) {
			player.getInventory().setItemInMainHand(item);
		}else {
			player.setItemInHand(item);
		}
	}
	
	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		}catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public static boolean isLong(String s) {
		try {
			Long.parseLong(s);
		}catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public static Player getPlayer(String name) {
		return Bukkit.getServer().getPlayer(name);
	}
	
	@SuppressWarnings("deprecation")
	public static OfflinePlayer getOfflinePlayer(String name) {
		return Bukkit.getServer().getOfflinePlayer(name);
	}
	
	public static Location getLoc(Player player) {
		return player.getLocation();
	}
	
	public static void runCMD(Player player, String CMD) {
		player.performCommand(CMD);
	}
	
	public static boolean isOnline(String name) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(player.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isOnline(String name, CommandSender p) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(player.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		p.sendMessage(color(Main.settings.getMsg().getString("Messages.Not-Online")));
		return false;
	}
	
	public static boolean hasPermission(Player player, String perm) {
		if(!player.hasPermission("CrazyAuctions." + perm)) {
			player.sendMessage(color(Main.settings.getMsg().getString("Messages.No-Permission")));
			return false;
		}
		return true;
	}
	
	public static boolean hasPermission(CommandSender sender, String perm) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!player.hasPermission("CrazyAuctions." + perm)) {
				player.sendMessage(color(Main.settings.getMsg().getString("Messages.No-Permission")));
				return false;
			}else {
				return true;
			}
		}else {
			return true;
		}
	}
	
	public static void hasUpdate() {
		try {
			HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=25219").getBytes("UTF-8"));
			String oldVersion = plugin.getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				Bukkit.getConsoleSender().sendMessage(getPrefix() + color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
			}
		}catch(Exception e) {
			return;
		}
	}
	
	public static void hasUpdate(Player player) {
		try {
			HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=25219").getBytes("UTF-8"));
			String oldVersion = plugin.getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				player.sendMessage(getPrefix() + color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
			}
		}catch(Exception e) {
			return;
		}
	}
	
	public static List<ItemStack> getPage(List<ItemStack> list, Integer page) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		if(page <= 0) page = 1;
		int max = 45;
		int index = page * max - max;
		int endIndex = index >= list.size() ? list.size() - 1 : index + max;
		for(; index < endIndex; index++) {
			if(index < list.size()) items.add(list.get(index));
		}
		for(; items.size() == 0; page--) {
			if(page <= 0) break;
			index = page * max - max;
			endIndex = index >= list.size() ? list.size() - 1 : index + max;
			for(; index < endIndex; index++) {
				if(index < list.size()) items.add(list.get(index));
			}
		}
		return items;
	}
	
	public static List<Integer> getPageInts(List<Integer> list, Integer page) {
		List<Integer> items = new ArrayList<Integer>();
		if(page <= 0) page = 1;
		int max = 45;
		int index = page * max - max;
		int endIndex = index >= list.size() ? list.size() - 1 : index + max;
		for(; index < endIndex; index++) {
			if(index < list.size()) items.add(list.get(index));
		}
		for(; items.size() == 0; page--) {
			if(page <= 0) break;
			index = page * max - max;
			endIndex = index >= list.size() ? list.size() - 1 : index + max;
			for(; index < endIndex; index++) {
				if(index < list.size()) items.add(list.get(index));
			}
		}
		return items;
	}
	
	public static int getMaxPage(List<ItemStack> list) {
		int maxPage = 1;
		int amount = list.size();
		for(; amount > 45; amount -= 45, maxPage++);
		return maxPage;
	}
	
	public static String convertToTime(long time) {
		Calendar C = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		int total = ((int) (cal.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
		int D = 0;
		int H = 0;
		int M = 0;
		int S = 0;
		for(; total > 86400; total -= 86400, D++);
		for(; total > 3600; total -= 3600, H++);
		for(; total > 60; total -= 60, M++);
		S += total;
		return D + "d " + H + "h " + M + "m " + S + "s ";
	}
	
	public static long convertToMill(String time) {
		Calendar cal = Calendar.getInstance();
		for(String i : time.split(" ")) {
			if(i.contains("D") || i.contains("d")) {
				cal.add(Calendar.DATE, Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")));
			}
			if(i.contains("H") || i.contains("h")) {
				cal.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")));
			}
			if(i.contains("M") || i.contains("m")) {
				cal.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")));
			}
			if(i.contains("S") || i.contains("s")) {
				cal.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("S", "").replaceAll("s", "")));
			}
		}
		return cal.getTimeInMillis();
	}
	
	public static boolean isInvFull(Player player) {
		if(player.getInventory().firstEmpty() == -1) {
			return true;
		}
		return false;
	}
	
	public static void updateAuction() {
		FileConfiguration data = Main.settings.getData();
		FileConfiguration msg = Main.settings.getMsg();
		Calendar cal = Calendar.getInstance();
		Calendar expireTime = Calendar.getInstance();
		Calendar fullExpireTime = Calendar.getInstance();
		if(data.contains("OutOfTime/Cancelled")) {
			for(String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
				fullExpireTime.setTimeInMillis(data.getLong("OutOfTime/Cancelled." + i + ".Full-Time"));
				if(cal.after(fullExpireTime)) {
					data.set("OutOfTime/Cancelled." + i, null);
				}
			}
		}
		if(data.contains("Items")) {
			for(String i : data.getConfigurationSection("Items").getKeys(false)) {
				expireTime.setTimeInMillis(data.getLong("Items." + i + ".Time-Till-Expire"));
				fullExpireTime.setTimeInMillis(data.getLong("Items." + i + ".Full-Time"));
				if(cal.after(expireTime)) {
					int num = 1;
					for(; data.contains("OutOfTime/Cancelled." + num); num++);
					if(data.getBoolean("Items." + i + ".Biddable") && !data.getString("Items." + i + ".TopBidder").equalsIgnoreCase("None") && CurrencyManager.getMoney(Methods.getPlayer(data.getString("Items." + i + ".TopBidder"))) >= data.getInt("Items." + i + ".Price")) {
						String winner = data.getString("Items." + i + ".TopBidder");
						String seller = data.getString("Items." + i + ".Seller");
						Long price = data.getLong("Items." + i + ".Price");
						CurrencyManager.addMoney(Methods.getOfflinePlayer(seller), price);
						CurrencyManager.removeMoney(Methods.getOfflinePlayer(winner), price);
						if(Methods.isOnline(winner)) {
							Player player = Methods.getPlayer(winner);
							Bukkit.getPluginManager().callEvent(new AuctionWinBidEvent(player, data.getItemStack("Items." + i + ".Item"), price));
							player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Win-Bidding").replaceAll("%Price%", getPrice(i, false)).replaceAll("%price%", getPrice(i, false))));
						}
						if(Methods.isOnline(seller)) {
							Player player = Methods.getPlayer(seller);
							player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Someone-Won-Players-Bid").replaceAll("%Price%", getPrice(i, false)).replaceAll("%price%", getPrice(i, false)).replaceAll("%Player%", winner).replaceAll("%player%", winner)));
						}
						data.set("OutOfTime/Cancelled." + num + ".Seller", winner);
						data.set("OutOfTime/Cancelled." + num + ".Full-Time", fullExpireTime.getTimeInMillis());
						data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
						data.set("OutOfTime/Cancelled." + num + ".Item", data.getItemStack("Items." + i + ".Item"));
					}else {
						String seller = data.getString("Items." + i + ".Seller");
						if(Methods.isOnline(seller)) {
							Player player = Methods.getPlayer(seller);
							player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Item-Has-Expired")));
						}
						data.set("OutOfTime/Cancelled." + num + ".Seller", data.getString("Items." + i + ".Seller"));
						data.set("OutOfTime/Cancelled." + num + ".Full-Time", fullExpireTime.getTimeInMillis());
						data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
						data.set("OutOfTime/Cancelled." + num + ".Item", data.getItemStack("Items." + i + ".Item"));
					}
					data.set("Items." + i, null);
				}
			}
		}
		Main.settings.saveData();
	}
	
	public static String getPrice(String ID, Boolean Expired) {
		Long price = 0L;
		if(Expired) {
			if(Main.settings.getData().contains("OutOfTime/Cancelled." + ID + ".Price")) {
				price = Main.settings.getData().getLong("OutOfTime/Cancelled." + ID + ".Price");
			}
		}else {
			if(Main.settings.getData().contains("Items." + ID + ".Price")) {
				price = Main.settings.getData().getLong("Items." + ID + ".Price");
			}
		}
		return String.valueOf(price);
	}
	
}
