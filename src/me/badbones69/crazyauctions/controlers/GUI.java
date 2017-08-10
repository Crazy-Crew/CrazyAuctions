package me.badbones69.crazyauctions.controlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.badbones69.crazyauctions.Main;
import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.api.Category;
import me.badbones69.crazyauctions.api.ShopType;
import me.badbones69.crazyauctions.currency.CurrencyManager;
import me.badbones69.crazyauctions.events.AuctionBuyEvent;
import me.badbones69.crazyauctions.events.AuctionNewBidEvent;

public class GUI implements Listener{
	
	private static HashMap<Player, Integer> Bidding = new HashMap<Player, Integer>();
	private static HashMap<Player, String> BiddingID = new HashMap<Player, String>();
	private static HashMap<Player, ShopType> Type = new HashMap<Player, ShopType>(); // Shop Type
	private static HashMap<Player, Category> Cat = new HashMap<Player, Category>(); // Category Type 
	private static HashMap<Player, List<Integer>> List = new HashMap<Player, List<Integer>>();
	private static HashMap<Player, String> IDs = new HashMap<Player, String>();
	
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyAuctions");
	
	public static void openShop(Player player, ShopType sell, Category cat, int page){
		Methods.updateAuction();
		FileConfiguration config = Main.settings.getConfig();
		FileConfiguration data = Main.settings.getData();
		List<ItemStack> items = new ArrayList<ItemStack>();
		List<Integer> ID = new ArrayList<Integer>();
		List<Integer> Id = new ArrayList<Integer>();
		if(!data.contains("Items")){
			data.set("Items.Clear", null);
			Main.settings.saveData();
		}
		if(cat != null){
			Cat.put(player, cat);
		}else{
			Cat.put(player, Category.NONE);
		}
		if(data.contains("Items")){
			for(String i : data.getConfigurationSection("Items").getKeys(false)){
				List<String> lore = new ArrayList<String>();
				if(cat.getItems().contains(data.getItemStack("Items."+i+".Item").getType()) || cat == Category.NONE){
					if(data.getBoolean("Items."+i+".Biddable")){
						if(sell==ShopType.BID){
							String seller = data.getString("Items."+i+".Seller");
							String topbidder = data.getString("Items."+i+".TopBidder");
							for(String l : config.getStringList("Settings.GUISettings.Bidding")){
								lore.add(l.replaceAll("%TopBid%", Methods.getPrice(i, false)).replaceAll("%topbid%", Methods.getPrice(i, false))
										.replaceAll("%Seller%", seller).replaceAll("%seller%", seller)
										.replaceAll("%TopBidder%", topbidder).replaceAll("%topbidder%", topbidder)
										.replaceAll("%Time%", Methods.convertToTime(data.getLong("Items."+i+".Time-Till-Expire"))).replaceAll("%time%", Methods.convertToTime(data.getLong("Items."+i+".Time-Till-Expire"))));
							}
							items.add(Methods.addLore(data.getItemStack("Items."+i+".Item").clone(), lore));
							ID.add(data.getInt("Items."+i+".StoreID"));
						}
					}else{
						if(sell==ShopType.SELL){
							for(String l : config.getStringList("Settings.GUISettings.SellingItemLore")){
								lore.add(l.replaceAll("%Price%", Methods.getPrice(i, false)).replaceAll("%price%", Methods.getPrice(i, false))
										.replaceAll("%Seller%", data.getString("Items."+i+".Seller")).replaceAll("%seller%", data.getString("Items."+i+".Seller")));
							}
							items.add(Methods.addLore(data.getItemStack("Items."+i+".Item").clone(), lore));
							ID.add(data.getInt("Items."+i+".StoreID"));
						}
					}
				}
			}
		}
		int maxPage = Methods.getMaxPage(items);
		for(;page>maxPage;page--);
		Inventory inv = Bukkit.createInventory(null, 54, Methods.color(config.getString("Settings.GUIName")+" #"+page));
		List<String> options = new ArrayList<String>();
		options.add("SellingItems");options.add("Cancelled/ExpiredItems");
		options.add("PreviousPage");options.add("Refesh");options.add("NextPage");
		options.add("Category1");options.add("Category2");
		if(sell==ShopType.SELL){
			Type.put(player, ShopType.SELL);
			options.add("Bidding/Selling.Selling");
			options.add("WhatIsThis.SellingShop");
		}
		if(sell==ShopType.BID){
			Type.put(player, ShopType.BID);
			options.add("Bidding/Selling.Bidding");
			options.add("WhatIsThis.BiddingShop");
		}
		for(String o : options){
			if(config.contains("Settings.GUISettings.OtherSettings."+o+".Toggle")){
				if(!config.getBoolean("Settings.GUISettings.OtherSettings."+o+".Toggle")){
					continue;
				}
			}
			String id = config.getString("Settings.GUISettings.OtherSettings."+o+".Item");
			String name = config.getString("Settings.GUISettings.OtherSettings."+o+".Name");
			List<String> lore = new ArrayList<String>();
			int slot = config.getInt("Settings.GUISettings.OtherSettings."+o+".Slot");
			String cName = Methods.color(config.getString("Settings.GUISettings.Category-Settings." + Cat.get(player).getName() + ".Name"));
			if(config.contains("Settings.GUISettings.OtherSettings."+o+".Lore")){
				for(String l : config.getStringList("Settings.GUISettings.OtherSettings."+o+".Lore")){
					lore.add(l.replaceAll("%Category%", cName).replaceAll("%category%", cName));
				}
				inv.setItem(slot-1, Methods.makeItem(id, 1, name, lore));
			}else{
				inv.setItem(slot-1, Methods.makeItem(id, 1, name));
			}
		}
		for(ItemStack item : Methods.getPage(items, page)){
			int slot = inv.firstEmpty();
			inv.setItem(slot, item);
		}
		for(int id : Methods.getPageInts(ID, page)){
			Id.add(id);
		}
		List.put(player, Id);
		player.openInventory(inv);
	}
	
	public static void openCateories(Player player, ShopType shop){
		Methods.updateAuction();
		FileConfiguration config = Main.settings.getConfig();
		Inventory inv = Bukkit.createInventory(null, 54, Methods.color(config.getString("Settings.Categories")));
		List<String> options = new ArrayList<String>();
		options.add("OtherSettings.Back");options.add("OtherSettings.WhatIsThis.Categories");
		options.add("Category-Settings.Armor");options.add("Category-Settings.Weapons");
		options.add("Category-Settings.Tools");options.add("Category-Settings.Food");
		options.add("Category-Settings.Potions");options.add("Category-Settings.Blocks");
		options.add("Category-Settings.Other");options.add("Category-Settings.None");
		for(String o : options){
			if(config.contains("Settings.GUISettings."+o+".Toggle")){
				if(!config.getBoolean("Settings.GUISettings."+o+".Toggle")){
					continue;
				}
			}
			String id = config.getString("Settings.GUISettings."+o+".Item");
			String name = config.getString("Settings.GUISettings."+o+".Name");
			int slot = config.getInt("Settings.GUISettings."+o+".Slot");
			if(config.contains("Settings.GUISettings."+o+".Lore")){
				inv.setItem(slot-1, Methods.makeItem(id, 1, name, config.getStringList("Settings.GUISettings."+o+".Lore")));
			}else{
				inv.setItem(slot-1, Methods.makeItem(id, 1, name));
			}
		}
		Type.put(player, shop);
		player.openInventory(inv);
	}
	
	public static void openPlayersCurrentList(Player player, int page){
		Methods.updateAuction();
		FileConfiguration config = Main.settings.getConfig();
		FileConfiguration data = Main.settings.getData();
		List<ItemStack> items = new ArrayList<ItemStack>();
		List<Integer> ID = new ArrayList<Integer>();
		List<Integer> Id = new ArrayList<Integer>();
		Inventory inv = Bukkit.createInventory(null, 54, Methods.color(config.getString("Settings.Players-Current-Items")));
		List<String> options = new ArrayList<String>();
		options.add("Back");options.add("WhatIsThis.CurrentItems");
		for(String o : options){
			if(config.contains("Settings.GUISettings.OtherSettings."+o+".Toggle")){
				if(!config.getBoolean("Settings.GUISettings.OtherSettings."+o+".Toggle")){
					continue;
				}
			}
			String id = config.getString("Settings.GUISettings.OtherSettings."+o+".Item");
			String name = config.getString("Settings.GUISettings.OtherSettings."+o+".Name");
			int slot = config.getInt("Settings.GUISettings.OtherSettings."+o+".Slot");
			if(config.contains("Settings.GUISettings.OtherSettings."+o+".Lore")){
				inv.setItem(slot-1, Methods.makeItem(id, 1, name, config.getStringList("Settings.GUISettings.OtherSettings."+o+".Lore")));
			}else{
				inv.setItem(slot-1, Methods.makeItem(id, 1, name));
			}
		}
		if(data.contains("Items")){
			for(String i : data.getConfigurationSection("Items").getKeys(false)){
				if(data.getString("Items."+i+".Seller").equalsIgnoreCase(player.getName())){
					List<String> lore = new ArrayList<String>();
					for(String l : config.getStringList("Settings.GUISettings.CurrentLore")){
						lore.add(l.replaceAll("%Price%", Methods.getPrice(i, false)).replaceAll("%price%", Methods.getPrice(i, false))
								.replaceAll("%Time%", Methods.convertToTime(data.getLong("Items."+i+".Time-Till-Expire"))).replaceAll("%time%", Methods.convertToTime(data.getLong("Items."+i+".Time-Till-Expire"))));
					}
					items.add(Methods.addLore(data.getItemStack("Items."+i+".Item").clone(), lore));
					ID.add(data.getInt("Items."+i+".StoreID"));
				}
			}
		}
		for(ItemStack item : Methods.getPage(items, page)){
			int slot = inv.firstEmpty();
			inv.setItem(slot, item);
		}
		for(int id : Methods.getPageInts(ID, page)){
			Id.add(id);
		}
		List.put(player, Id);
		player.openInventory(inv);
	}
	
	public static void openPlayersExpiredList(Player player, int page){
		Methods.updateAuction();
		FileConfiguration config = Main.settings.getConfig();
		FileConfiguration data = Main.settings.getData();
		List<ItemStack> items = new ArrayList<ItemStack>();
		List<Integer> ID = new ArrayList<Integer>();
		List<Integer> Id = new ArrayList<Integer>();
		if(data.contains("OutOfTime/Cancelled")){
			for(String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)){
				if(data.getString("OutOfTime/Cancelled."+i+".Seller").equalsIgnoreCase(player.getName())){
					List<String> lore = new ArrayList<String>();
					for(String l : config.getStringList("Settings.GUISettings.Cancelled/ExpiredLore")){
						lore.add(l.replaceAll("%Price%", Methods.getPrice(i, true)).replaceAll("%price%", Methods.getPrice(i, true))
								.replaceAll("%Time%", Methods.convertToTime(data.getLong("OutOfTime/Cancelled."+i+".Full-Time"))).replaceAll("%time%", Methods.convertToTime(data.getLong("OutOfTime/Cancelled."+i+".Full-Time"))));
					}
					items.add(Methods.addLore(data.getItemStack("OutOfTime/Cancelled."+i+".Item").clone(), lore));
					ID.add(data.getInt("OutOfTime/Cancelled."+i+".StoreID"));
				}
			}
		}
		int maxPage = Methods.getMaxPage(items);
		for(;page>maxPage;page--);
		Inventory inv = Bukkit.createInventory(null, 54, Methods.color(config.getString("Settings.Cancelled/Expired-Items")+" #"+page));
		List<String> options = new ArrayList<String>();
		options.add("Back");
		options.add("PreviousPage");options.add("Return");options.add("NextPage");
		options.add("WhatIsThis.Cancelled/ExpiredItems");
		for(String o : options){
			if(config.contains("Settings.GUISettings.OtherSettings."+o+".Toggle")){
				if(!config.getBoolean("Settings.GUISettings.OtherSettings."+o+".Toggle")){
					continue;
				}
			}
			String id = config.getString("Settings.GUISettings.OtherSettings."+o+".Item");
			String name = config.getString("Settings.GUISettings.OtherSettings."+o+".Name");
			int slot = config.getInt("Settings.GUISettings.OtherSettings."+o+".Slot");
			if(config.contains("Settings.GUISettings.OtherSettings."+o+".Lore")){
				inv.setItem(slot-1, Methods.makeItem(id, 1, name, config.getStringList("Settings.GUISettings.OtherSettings."+o+".Lore")));
			}else{
				inv.setItem(slot-1, Methods.makeItem(id, 1, name));
			}
		}
		for(ItemStack item : Methods.getPage(items, page)){
			int slot = inv.firstEmpty();
			inv.setItem(slot, item);
		}
		for(int id : Methods.getPageInts(ID, page)){
			Id.add(id);
		}
		List.put(player, Id);
		player.openInventory(inv);
	}
	
	public static void openBuying(Player player, String ID){
		Methods.updateAuction();
		FileConfiguration config = Main.settings.getConfig();
		FileConfiguration data = Main.settings.getData();
		FileConfiguration msg = Main.settings.getMsg();
		if(!data.contains("Items."+ID)){
			openShop(player, ShopType.SELL, Cat.get(player), 1);
			player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Item-Doesnt-Exist")));
			return;
		}
		Inventory inv = Bukkit.createInventory(null, 9, Methods.color(config.getString("Settings.Buying-Item")));
		List<String> options = new ArrayList<String>();
		options.add("Confirm");options.add("Cancel");
		for(String o : options){
			String id = config.getString("Settings.GUISettings.OtherSettings."+o+".Item");
			String name = config.getString("Settings.GUISettings.OtherSettings."+o+".Name");
			ItemStack item = new ItemStack(Material.AIR);
			if(config.contains("Settings.GUISettings.OtherSettings."+o+".Lore")){
				item = Methods.makeItem(id, 1, name, config.getStringList("Settings.GUISettings.OtherSettings."+o+".Lore"));
			}else{
				item = Methods.makeItem(id, 1, name);
			}
			if(o.equals("Confirm")){
				inv.setItem(0, item);
				inv.setItem(1, item);
				inv.setItem(2, item);
				inv.setItem(3, item);
			}
			if(o.equals("Cancel")){
				inv.setItem(5, item);
				inv.setItem(6, item);
				inv.setItem(7, item);
				inv.setItem(8, item);
			}
		}
		ItemStack item = data.getItemStack("Items."+ID+".Item");
		List<String> lore = new ArrayList<String>();
		for(String l : config.getStringList("Settings.GUISettings.SellingLore")){
			lore.add(l.replaceAll("%Price%", Methods.getPrice(ID, false)).replaceAll("%price%", Methods.getPrice(ID, false))
					.replaceAll("%Seller%", data.getString("Items."+ID+".Seller")).replaceAll("%seller%", data.getString("Items."+ID+".Seller")));
		}
		inv.setItem(4, Methods.addLore(item.clone(), lore));
		IDs.put(player, ID);
		player.openInventory(inv);
	}
	
	public static void openBidding(Player player, String ID){
		Methods.updateAuction();
		FileConfiguration config = Main.settings.getConfig();
		FileConfiguration data = Main.settings.getData();
		FileConfiguration msg = Main.settings.getMsg();
		if(!data.contains("Items."+ID)){
			openShop(player, ShopType.BID, Cat.get(player), 1);
			player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Item-Doesnt-Exist")));
			return;
		}
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(config.getString("Settings.Bidding-On-Item")));
		if(!Bidding.containsKey(player))Bidding.put(player, 0);
		inv.setItem(9, Methods.makeItem("160:5", 1, "&a+1"));
		inv.setItem(10, Methods.makeItem("160:5", 1, "&a+10"));
		inv.setItem(11, Methods.makeItem("160:5", 1, "&a+100"));
		inv.setItem(12, Methods.makeItem("160:5", 1, "&a+1000"));
		inv.setItem(13, getBiddingGlass(player, ID));
		inv.setItem(14, Methods.makeItem("160:14", 1, "&c-1000"));
		inv.setItem(15, Methods.makeItem("160:14", 1, "&c-100"));
		inv.setItem(16, Methods.makeItem("160:14", 1, "&c-10"));
		inv.setItem(17, Methods.makeItem("160:14", 1, "&c-1"));
		inv.setItem(22, Methods.makeItem(config.getString("Settings.GUISettings.OtherSettings.Bid.Item"), 1, config.getString("Settings.GUISettings.OtherSettings.Bid.Name"), 
				config.getStringList("Settings.GUISettings.OtherSettings.Bid.Lore")));
		
		inv.setItem(4, getBiddingItem(player, ID));
		player.openInventory(inv);
	}
	
	public static void openViewer(Player player, String other, int page){
		Methods.updateAuction();
		FileConfiguration config = Main.settings.getConfig();
		FileConfiguration data = Main.settings.getData();
		List<ItemStack> items = new ArrayList<ItemStack>();
		List<Integer> ID = new ArrayList<Integer>();
		List<Integer> Id = new ArrayList<Integer>();
		if(!data.contains("Items")){
			data.set("Items.Clear", null);
			Main.settings.saveData();
		}
		if(data.contains("Items")){
			for(String i : data.getConfigurationSection("Items").getKeys(false)){
				if(data.getString("Items."+i+".Seller").equalsIgnoreCase(other)){
					List<String> lore = new ArrayList<String>();
					if(data.getBoolean("Items."+i+".Biddable")){
						String seller = data.getString("Items."+i+".Seller");
						String topbidder = data.getString("Items."+i+".TopBidder");
						for(String l : config.getStringList("Settings.GUISettings.Bidding")){
							lore.add(l.replaceAll("%TopBid%", Methods.getPrice(i, false)).replaceAll("%topbid%", Methods.getPrice(i, false))
									.replaceAll("%Seller%", seller).replaceAll("%seller%", seller)
									.replaceAll("%TopBidder%", topbidder).replaceAll("%topbidder%", topbidder)
									.replaceAll("%Time%", Methods.convertToTime(data.getLong("Items."+i+".Time-Till-Expire"))).replaceAll("%time%", Methods.convertToTime(data.getLong("Items."+i+".Time-Till-Expire"))));
						}
						items.add(Methods.addLore(data.getItemStack("Items."+i+".Item").clone(), lore));
						ID.add(data.getInt("Items."+i+".StoreID"));
					}else{
						for(String l : config.getStringList("Settings.GUISettings.SellingItemLore")){
							lore.add(l.replaceAll("%Price%", Methods.getPrice(i, false)).replaceAll("%price%", Methods.getPrice(i, false))
									.replaceAll("%Seller%", data.getString("Items."+i+".Seller")).replaceAll("%seller%", data.getString("Items."+i+".Seller")));
						}
						items.add(Methods.addLore(data.getItemStack("Items."+i+".Item").clone(), lore));
						ID.add(data.getInt("Items."+i+".StoreID"));
					}
				}
			}
		}
		int maxPage = Methods.getMaxPage(items);
		for(;page>maxPage;page--);
		Inventory inv = Bukkit.createInventory(null, 54, Methods.color(config.getString("Settings.GUIName")+" #"+page));
		List<String> options = new ArrayList<String>();
		options.add("WhatIsThis.Viewing");
		for(String o : options){
			if(config.contains("Settings.GUISettings.OtherSettings."+o+".Toggle")){
				if(!config.getBoolean("Settings.GUISettings.OtherSettings."+o+".Toggle")){
					continue;
				}
			}
			String id = config.getString("Settings.GUISettings.OtherSettings."+o+".Item");
			String name = config.getString("Settings.GUISettings.OtherSettings."+o+".Name");
			int slot = config.getInt("Settings.GUISettings.OtherSettings."+o+".Slot");
			if(config.contains("Settings.GUISettings.OtherSettings."+o+".Lore")){
				inv.setItem(slot-1, Methods.makeItem(id, 1, name, config.getStringList("Settings.GUISettings.OtherSettings."+o+".Lore")));
			}else{
				inv.setItem(slot-1, Methods.makeItem(id, 1, name));
			}
		}
		for(ItemStack item : Methods.getPage(items, page)){
			int slot = inv.firstEmpty();
			inv.setItem(slot, item);
		}
		for(int id : Methods.getPageInts(ID, page)){
			Id.add(id);
		}
		List.put(player, Id);
		player.openInventory(inv);
	}
	
	public static ItemStack getBiddingGlass(Player player, String ID){
		FileConfiguration config = Main.settings.getConfig();
		String id = config.getString("Settings.GUISettings.OtherSettings.Bidding.Item");
		String name = config.getString("Settings.GUISettings.OtherSettings.Bidding.Name");
		ItemStack item = new ItemStack(Material.AIR);
		int bid = Bidding.get(player);
		if(config.contains("Settings.GUISettings.OtherSettings.Bidding.Lore")){
			List<String> lore = new ArrayList<String>();
			for(String l : config.getStringList("Settings.GUISettings.OtherSettings.Bidding.Lore")){
				lore.add(l.replaceAll("%Bid%", bid+"").replaceAll("%bid%", bid+"")
						.replaceAll("%TopBid%", Methods.getPrice(ID, false)).replaceAll("%topbid%", Methods.getPrice(ID, false)));
			}
			item = Methods.makeItem(id, 1, name, lore);
		}else{
			item = Methods.makeItem(id, 1, name);
		}
		return item;
	}
	
	public static ItemStack getBiddingItem(Player player, String ID){
		FileConfiguration config = Main.settings.getConfig();
		FileConfiguration data = Main.settings.getData();
		String seller = data.getString("Items."+ID+".Seller");
		String topbidder = data.getString("Items."+ID+".TopBidder");
		ItemStack item = data.getItemStack("Items."+ID+".Item");
		List<String> lore = new ArrayList<String>();
		for(String l : config.getStringList("Settings.GUISettings.Bidding")){
			lore.add(l.replaceAll("%TopBid%", Methods.getPrice(ID, false)).replaceAll("%topbid%", Methods.getPrice(ID, false))
					.replaceAll("%Seller%", seller).replaceAll("%seller%", seller)
					.replaceAll("%TopBidder%", topbidder).replaceAll("%topbidder%", topbidder)
					.replaceAll("%Time%", Methods.convertToTime(data.getLong("Items."+ID+".Time-Till-Expire"))).replaceAll("%time%", Methods.convertToTime(data.getLong("Items."+ID+".Time-Till-Expire"))));
		}
		return Methods.addLore(item.clone(), lore);
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e){
		FileConfiguration config = Main.settings.getConfig();
		Inventory inv = e.getInventory();
		Player player = (Player) e.getPlayer();
		if(inv!=null){
			if(inv.getName().contains(Methods.color(config.getString("Settings.Bidding-On-Item")))){
				if(Bidding.containsKey(player)){
					Bidding.remove(player);
				}
			}
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		FileConfiguration config = Main.settings.getConfig();
		FileConfiguration data = Main.settings.getData();
		FileConfiguration msg = Main.settings.getMsg();
		Player player = (Player)e.getWhoClicked();
		final Inventory inv = e.getInventory();
		if(inv!=null){
			if(inv.getName().contains(Methods.color(config.getString("Settings.Categories")))){
				e.setCancelled(true);
				int slot = e.getRawSlot();
				if(slot<=inv.getSize()){
					if(e.getCurrentItem()!=null){
						ItemStack item = e.getCurrentItem();
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasDisplayName()){
								for(Category cat : Category.values()){
									if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.Category-Settings."+cat.getName()+".Name")))){
										openShop(player, Type.get(player), cat, 1);
										playClick(player);
										return;
									}
									if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Back.Name")))){
										openShop(player, Type.get(player), Cat.get(player), 1);
										playClick(player);
										return;
									}
								}
							}
						}
					}
				}
			}
			if(inv.getName().contains(Methods.color(config.getString("Settings.Bidding-On-Item")))){
				e.setCancelled(true);
				int slot = e.getRawSlot();
				if(slot<=inv.getSize()){
					if(e.getCurrentItem()!=null){
						ItemStack item = e.getCurrentItem();
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasDisplayName()){
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bid.Name")))){
									String ID = BiddingID.get(player);
									int bid = Bidding.get(player);
									String topBidder = data.getString("Items."+ID+".TopBidder");
									if(CurrencyManager.getMoney(player)<bid){
										player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Need-More-Money")
												.replaceAll("%Money_Needed%", (bid-CurrencyManager.getMoney(player))+"").replaceAll("%money_needed%", (bid-CurrencyManager.getMoney(player))+"")));
										return;
									}
									if(data.getLong("Items."+ID+".Price")>bid){
										player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Bid-More-Money")));
										return;
									}
									if(data.getLong("Items."+ID+".Price")>=bid&&!topBidder.equalsIgnoreCase("None")){
										player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Bid-More-Money")));
										return;
									}
									Bukkit.getPluginManager().callEvent(new AuctionNewBidEvent(player, data.getItemStack("Items."+ID+".Item"), bid));
									data.set("Items."+ID+".Price", bid);
									data.set("Items."+ID+".TopBidder", player.getName());
									player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Bid-Msg")
											.replaceAll("%Bid%", bid+"").replaceAll("%bid%", bid+"")));
									Main.settings.saveData();
									Bidding.put(player, 0);
									player.closeInventory();
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color("&a+1"))){
									Bidding.put(player, (Bidding.get(player)+1));
									inv.setItem(4, getBiddingItem(player, BiddingID.get(player)));
									inv.setItem(13, getBiddingGlass(player, BiddingID.get(player)));
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color("&a+10"))){
									Bidding.put(player, (Bidding.get(player)+10));
									inv.setItem(4, getBiddingItem(player, BiddingID.get(player)));
									inv.setItem(13, getBiddingGlass(player, BiddingID.get(player)));
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color("&a+100"))){
									Bidding.put(player, (Bidding.get(player)+100));
									inv.setItem(4, getBiddingItem(player, BiddingID.get(player)));
									inv.setItem(13, getBiddingGlass(player, BiddingID.get(player)));
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color("&a+1000"))){
									Bidding.put(player, (Bidding.get(player)+1000));
									inv.setItem(4, getBiddingItem(player, BiddingID.get(player)));
									inv.setItem(13, getBiddingGlass(player, BiddingID.get(player)));
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color("&c-1"))){
									int bid = Bidding.get(player)-1;
									if(bid<0)bid=0;
									Bidding.put(player, bid);
									inv.setItem(4, getBiddingItem(player, BiddingID.get(player)));
									inv.setItem(13, getBiddingGlass(player, BiddingID.get(player)));
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color("&c-10"))){
									int bid = Bidding.get(player)-10;
									if(bid<0)bid=0;
									Bidding.put(player, bid);
									inv.setItem(4, getBiddingItem(player, BiddingID.get(player)));
									inv.setItem(13, getBiddingGlass(player, BiddingID.get(player)));
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color("&c-100"))){
									int bid = Bidding.get(player)-100;
									if(bid<0)bid=0;
									Bidding.put(player, bid);
									inv.setItem(4, getBiddingItem(player, BiddingID.get(player)));
									inv.setItem(13, getBiddingGlass(player, BiddingID.get(player)));
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color("&c-1000"))){
									int bid = Bidding.get(player)-1000;
									if(bid<0)bid=0;
									Bidding.put(player, bid);
									inv.setItem(4, getBiddingItem(player, BiddingID.get(player)));
									inv.setItem(13, getBiddingGlass(player, BiddingID.get(player)));
									playClick(player);
									return;
								}
							}
						}
					}
				}
			}
			if(inv.getName().contains(Methods.color(config.getString("Settings.GUIName")))){
				e.setCancelled(true);
				final int slot = e.getRawSlot();
				if(slot<=inv.getSize()){
					if(e.getCurrentItem()!=null){
						final ItemStack item = e.getCurrentItem();
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasDisplayName()){
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.NextPage.Name")))){
									Methods.updateAuction();
									int page = Integer.parseInt(inv.getName().split("#")[1]);
									openShop(player, Type.get(player), Cat.get(player), page+1);
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.PreviousPage.Name")))){
									Methods.updateAuction();
									int page = Integer.parseInt(inv.getName().split("#")[1]);
									if(page==1)page++;
									openShop(player, Type.get(player), Cat.get(player), page-1);
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Refesh.Name")))){
									Methods.updateAuction();
									int page = Integer.parseInt(inv.getName().split("#")[1]);
									openShop(player, Type.get(player), Cat.get(player), page);
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bidding/Selling.Selling.Name")))){
									openShop(player, ShopType.BID, Cat.get(player), 1);
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Bidding/Selling.Bidding.Name")))){
									openShop(player, ShopType.SELL, Cat.get(player), 1);
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cancelled/ExpiredItems.Name")))){
									openPlayersExpiredList(player, 1);
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.SellingItems.Name")))){
									openPlayersCurrentList(player, 1);
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Category1.Name")))){
									openCateories(player, Type.get(player));
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Category2.Name")))){
									openCateories(player, Type.get(player));
									playClick(player);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Your-Item.Name")))){
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Name")))){
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Name")))){
									return;
								}
							}
							if(List.get(player).size()>=slot){
								int id = List.get(player).get(slot);
								boolean T=false;
								if(data.contains("Items")){
									for(String i : data.getConfigurationSection("Items").getKeys(false)){
										int ID = data.getInt("Items."+i+".StoreID");
										if(id==ID){
											T=true;
											if(player.hasPermission("CrazyAuctions.Admin")){
												if(e.getAction()==InventoryAction.MOVE_TO_OTHER_INVENTORY){
													int num = 1;
													for(;data.contains("OutOfTime/Cancelled."+num);num++);
													String seller = data.getString("Items."+i+".Seller");
													if(Methods.isOnline(seller)){
														Player S = Methods.getPlayer(seller);
														S.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Admin-Force-Cancelled-To-Player")));
													}
													data.set("OutOfTime/Cancelled."+num+".Seller", data.getString("Items."+i+".Seller"));
													data.set("OutOfTime/Cancelled."+num+".Full-Time", data.getLong("Items."+i+".Full-Time"));
													data.set("OutOfTime/Cancelled."+num+".StoreID", data.getInt("Items."+i+".StoreID"));
													data.set("OutOfTime/Cancelled."+num+".Item", data.getItemStack("Items."+i+".Item"));
													data.set("Items."+i, null);
													Main.settings.saveData();
													player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Admin-Force-Cancelled")));
													playClick(player);
													int page = Integer.parseInt(inv.getName().split("#")[1]);
													openShop(player, Type.get(player), Cat.get(player), page);
													return;
												}
											}
											if(data.getString("Items."+i+".Seller").equalsIgnoreCase(player.getName())){
												String it = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Item");
												String name = config.getString("Settings.GUISettings.OtherSettings.Your-Item.Name");
												ItemStack I = new ItemStack(Material.AIR);
												if(config.contains("Settings.GUISettings.OtherSettings.Your-Item.Lore")){
													I = Methods.makeItem(it, 1, name, config.getStringList("Settings.GUISettings.OtherSettings.Your-Item.Lore"));
												}else{
													I = Methods.makeItem(it, 1, name);
												}
												inv.setItem(slot, I);
												playClick(player);
												Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
													@Override
													public void run() {
														inv.setItem(slot, item);
													}
												}, 3*20);
												return;
											}
											Long cost = data.getLong("Items."+i+".Price");
											if(CurrencyManager.getMoney(player)<cost){
												String it = config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Item");
												String name = config.getString("Settings.GUISettings.OtherSettings.Cant-Afford.Name");
												ItemStack I = new ItemStack(Material.AIR);
												if(config.contains("Settings.GUISettings.OtherSettings.Cant-Afford.Lore")){
													I = Methods.makeItem(it, 1, name, config.getStringList("Settings.GUISettings.OtherSettings.Cant-Afford.Lore"));
												}else{
													I = Methods.makeItem(it, 1, name);
												}
												inv.setItem(slot, I);
												playClick(player);
												Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
													@Override
													public void run() {
														inv.setItem(slot, item);
													}
												}, 3*20);
												return;
											}
											if(data.getBoolean("Items."+i+".Biddable")){
												if(player.getName().equalsIgnoreCase(data.getString("Items."+i+".TopBidder"))){
													String it = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Item");
													String name = config.getString("Settings.GUISettings.OtherSettings.Top-Bidder.Name");
													ItemStack I = new ItemStack(Material.AIR);
													if(config.contains("Settings.GUISettings.OtherSettings.Top-Bidder.Lore")){
														I = Methods.makeItem(it, 1, name, config.getStringList("Settings.GUISettings.OtherSettings.Top-Bidder.Lore"));
													}else{
														I = Methods.makeItem(it, 1, name);
													}
													inv.setItem(slot, I);
													playClick(player);
													Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
														@Override
														public void run() {
															inv.setItem(slot, item);
														}
													}, 3*20);
													return;
												}
												playClick(player);
												openBidding(player, i);
												BiddingID.put(player, i);
											}else{
												playClick(player);
												openBuying(player, i);
											}
											return;
										}
									}
								}
								if(!T){
									playClick(player);
									openShop(player, Type.get(player), Cat.get(player), 1);
									player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Item-Doesnt-Exist")));
									return;
								}
							}
						}
					}
				}
			}
			if(inv.getName().contains(Methods.color(config.getString("Settings.Buying-Item")))){
				e.setCancelled(true);
				int slot = e.getRawSlot();
				if(slot<=inv.getSize()){
					if(e.getCurrentItem()!=null){
						ItemStack item = e.getCurrentItem();
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasDisplayName()){
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Confirm.Name")))){
									String ID = IDs.get(player);
									long cost = data.getLong("Items."+ID+".Price");
									String seller = data.getString("Items."+ID+".Seller");
									if(!data.contains("Items."+ID)){
										playClick(player);
										openShop(player, Type.get(player), Cat.get(player), 1);
										player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Item-Doesnt-Exist")));
										return;
									}
									if(Methods.isInvFull(player)){
										playClick(player);
										player.closeInventory();
										player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Inventory-Full")));
										return;
									}
									if(CurrencyManager.getMoney(player)<cost){
										playClick(player);
										player.closeInventory();
										player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Need-More-Money")
												.replaceAll("%Money_Needed%", (cost-CurrencyManager.getMoney(player))+"").replaceAll("%money_needed%", (cost-CurrencyManager.getMoney(player))+"")));
										return;
									}
									ItemStack i = data.getItemStack("Items."+ID+".Item");
									Bukkit.getPluginManager().callEvent(new AuctionBuyEvent(player, i, cost));
									CurrencyManager.removeMoney(player, cost);
									CurrencyManager.addMoney(Methods.getOfflinePlayer(seller), cost);
									player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Bought-Item")
											.replaceAll("%Price%", Methods.getPrice(ID, false)).replaceAll("%price%", Methods.getPrice(ID, false))));
									if(Methods.isOnline(seller)){
										Player sell = Methods.getPlayer(seller);
										sell.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Player-Bought-Item")
												.replaceAll("%Price%", Methods.getPrice(ID, false)).replaceAll("%price%", Methods.getPrice(ID, false))
												.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
									}
									player.getInventory().addItem(i);
									data.set("Items."+ID, null);
									Main.settings.saveData();
									playClick(player);
									openShop(player, Type.get(player), Cat.get(player), 1);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Cancel.Name")))){
									openShop(player, Type.get(player), Cat.get(player), 1);
									playClick(player);
									return;
								}
							}
						}
					}
				}
			}
			if(inv.getName().contains(Methods.color(config.getString("Settings.Players-Current-Items")))){
				e.setCancelled(true);
				int slot = e.getRawSlot();
				if(slot<=inv.getSize()){
					if(e.getCurrentItem()!=null){
						ItemStack item = e.getCurrentItem();
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasDisplayName()){
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Back.Name")))){
									openShop(player, Type.get(player), Cat.get(player), 1);
									playClick(player);
									return;
								}
							}
							if(List.get(player).size()>=slot){
								int id = List.get(player).get(slot);
								boolean T=false;
								if(data.contains("Items")){
									for(String i : data.getConfigurationSection("Items").getKeys(false)){
										int ID = data.getInt("Items."+i+".StoreID");
										if(id==ID){
											T=true;
											player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Cancelled-Item")));
											int num = 1;
											for(;data.contains("OutOfTime/Cancelled."+num);num++);
											data.set("OutOfTime/Cancelled."+num+".Seller", data.getString("Items."+i+".Seller"));
											data.set("OutOfTime/Cancelled."+num+".Full-Time", data.getLong("Items."+i+".Full-Time"));
											data.set("OutOfTime/Cancelled."+num+".StoreID", data.getInt("Items."+i+".StoreID"));
											data.set("OutOfTime/Cancelled."+num+".Item", data.getItemStack("Items."+i+".Item"));
											data.set("Items."+i, null);
											Main.settings.saveData();
											playClick(player);
											openPlayersCurrentList(player, 1);
											return;
										}
									}
								}
								if(!T){
									playClick(player);
									openShop(player, Type.get(player), Cat.get(player), 1);
									player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Item-Doesnt-Exist")));
									return;
								}
							}
						}
					}
				}
			}
			if(inv.getName().contains(Methods.color(config.getString("Settings.Cancelled/Expired-Items")))){
				e.setCancelled(true);
				final int slot = e.getRawSlot();
				if(slot<=inv.getSize()){
					if(e.getCurrentItem()!=null){
						final ItemStack item = e.getCurrentItem();
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasDisplayName()){
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Back.Name")))){
									Methods.updateAuction();
									playClick(player);
									openShop(player, Type.get(player), Cat.get(player), 1);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.PreviousPage.Name")))){
									Methods.updateAuction();
									int page = Integer.parseInt(inv.getName().split("#")[1]);
									if(page==1)page++;
									playClick(player);
									openPlayersExpiredList(player, (page-1));
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.Return.Name")))){
									Methods.updateAuction();
									int page = Integer.parseInt(inv.getName().split("#")[1]);
									if(data.contains("OutOfTime/Cancelled")){
										for(String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)){
											if(data.getString("OutOfTime/Cancelled."+i+".Seller").equalsIgnoreCase(player.getName())){
												if(Methods.isInvFull(player)){
													player.sendMessage(Methods.getPrefix()+Methods.color(Main.settings.getMsg().getString("Messages.Inventory-Full")));
													break;
												}else{
													player.getInventory().addItem(data.getItemStack("OutOfTime/Cancelled."+i+".Item"));
													data.set("OutOfTime/Cancelled."+i, null);
												}
											}
										}
									}
									player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Got-Item-Back")));
									Main.settings.saveData();
									playClick(player);
									openPlayersExpiredList(player, page);
									return;
								}
								if(item.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.GUISettings.OtherSettings.NextPage.Name")))){
									Methods.updateAuction();
									int page = Integer.parseInt(inv.getName().split("#")[1]);
									playClick(player);
									openPlayersExpiredList(player, (page+1));
									return;
								}
							}
							if(List.get(player).size()>=slot){
								int id = List.get(player).get(slot);
								boolean T=false;
								if(data.contains("OutOfTime/Cancelled")){
									for(String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)){
										int ID = data.getInt("OutOfTime/Cancelled."+i+".StoreID");
										if(id==ID){
											if(!Methods.isInvFull(player)){
												T=true;
												player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Got-Item-Back")));
												ItemStack IT = data.getItemStack("OutOfTime/Cancelled."+i+".Item");
												player.getInventory().addItem(IT);
												data.set("OutOfTime/Cancelled."+i, null);
												Main.settings.saveData();
												playClick(player);
												openPlayersExpiredList(player, 1);
												return;
											}else{
												player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Inventory-Full")));
												return;
											}
										}
									}
								}
								if(!T){
									playClick(player);
									openShop(player, Type.get(player), Cat.get(player), 1);
									player.sendMessage(Methods.getPrefix()+Methods.color(msg.getString("Messages.Item-Doesnt-Exist")));
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static void playClick(Player player){
		if(Main.settings.getConfig().contains("Settings.Sounds.Toggle")){
			if(Main.settings.getConfig().getBoolean("Settings.Sounds.Toggle")){
				String sound = Main.settings.getConfig().getString("Settings.Sounds.Sound");
				try{
					player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 1);
				}catch(Exception e){
					if(Methods.getVersion()>=191){
						player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
					}else{
						player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}
					Bukkit.getLogger().log(Level.WARNING, "[Crazy Auctions]>> You set the sound to "+sound+" and this is not a sound for your minecraft version. "
							+ "Please go to the config and set a correct sound or turn the sound off in the toggle setting.");
				}
			}
		}else{
			if(Methods.getVersion()>=191){
				player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
			}else{
				player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
			}
		}
	}
	
}