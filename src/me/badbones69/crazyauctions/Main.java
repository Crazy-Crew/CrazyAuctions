package me.badbones69.crazyauctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.badbones69.crazyauctions.currency.Vault;

public class Main extends JavaPlugin implements Listener{
	
	public static SettingsManager settings = SettingsManager.getInstance();
	public static CrazyAuctions auc = CrazyAuctions.getInstance();
	private int file = 0;
	
	@Override
	public void onEnable(){
		saveDefaultConfig();
		settings.setup(this);
		Methods.hasUpdate();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(new GUI(), this);
		Methods.updateAuction();
		startCheck();
		if (!Vault.setupEconomy()){
	   		saveDefaultConfig();
	    }
		if(Bukkit.getPluginManager().getPlugin("Vault") == null){
			Bukkit.getConsoleSender().sendMessage(Methods.getPrefix()+
					Methods.color("&cThis plugin is shutting down. This plugin requires a compatable currency plugin."
					+ " &cPlease add Vault to continue using this."));
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
		try {
			new MCUpdate(this, true);
		} catch (IOException e) {}
	}
	
	@Override
	public void onDisable(){
		Bukkit.getScheduler().cancelTask(file);
		settings.saveData();
	}
	
        @Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args){
		if(commandLable.equalsIgnoreCase("CrazyAuctions") || commandLable.equalsIgnoreCase("CrazyAuction")
				|| commandLable.equalsIgnoreCase("CA") || commandLable.equalsIgnoreCase("AH")
				|| commandLable.equalsIgnoreCase("HDV")){
			if(args.length == 0){
				if(!Methods.hasPermission(sender, "Access"))return true;
				if(!(sender instanceof Player)){
					sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
					return true;
				}
				Player player = (Player) sender;
				if(settings.getConfig().contains("Settings.Category-Page-Opens-First")){
					if(settings.getConfig().getBoolean("Settings.Category-Page-Opens-First")){
						GUI.openCateories(player, Shop.SELL);
						return true;
					}
				}
				GUI.openShop(player, Shop.SELL, Category.NONE, 1);
				return true;
			}
			if(args.length >= 1){
				if(args[0].equalsIgnoreCase("Help")){// CA Help
					if(!Methods.hasPermission(sender, "Access"))return true;
					for(String msg : settings.getMsg().getStringList("Messages.Help-Menu")){
						sender.sendMessage(Methods.color(msg));
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("Reload")){// CA Reload
					if(!Methods.hasPermission(sender, "Admin"))return true;
					settings.reloadConfig();
					settings.reloadData();
					settings.reloadMsg();
					settings.setup(this);
					sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Reload")));
					return true;
				}
				if(args[0].equalsIgnoreCase("View")){// CA Reload
					if(!Methods.hasPermission(sender, "View"))return true;
					if(!(sender instanceof Player)){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
						return true;
					}
					if(args.length>=2){
						Player player = (Player) sender;
						GUI.openViewer(player, args[1], 1);
						return true;
					}
					sender.sendMessage(Methods.getPrefix()+Methods.color("&c/CA View <Player>"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Expired")||args[0].equalsIgnoreCase("Collect")){// CA Expired
					if(!Methods.hasPermission(sender, "Access"))return true;
					if(!(sender instanceof Player)){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
						return true;
					}
					Player player = (Player) sender;
					GUI.openPlayersExpiredList(player, 1);
					return true;
				}
				if(args[0].equalsIgnoreCase("Listed")){// CA Listed
					if(!Methods.hasPermission(sender, "Access"))return true;
					if(!(sender instanceof Player)){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
						return true;
					}
					Player player = (Player) sender;
					GUI.openPlayersCurrentList(player, 1);
					return true;
				}
				if(args[0].equalsIgnoreCase("Sell")||args[0].equalsIgnoreCase("Bid")){// /CA Sell/Bid <Price> [Amount of Items]
					if(!(sender instanceof Player)){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
						return true;
					}
					if(args.length >= 2){
						Player player = (Player) sender;
						if(args[0].equalsIgnoreCase("Sell")){
							if(!Methods.hasPermission(player, "Sell"))return true;
						}
						if(args[0].equalsIgnoreCase("Bid")){
							if(!Methods.hasPermission(player, "Bid"))return true;
						}
						ItemStack item = Methods.getItemInHand(player);
						int amount = item.getAmount();
						if(args.length >= 3){
							if(!Methods.isInt(args[2])){
								player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Number")
										.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
								return true;
							}
							amount=Integer.parseInt(args[2]);
							if(amount<=0)amount=1;
							if(amount>item.getAmount())amount=item.getAmount();
						}
						if(!Methods.isLong(args[1])){
							player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[1]).replaceAll("%arg%", args[1])));
							return true;
						}
						if(Methods.getItemInHand(player).getType() == Material.AIR){
							player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Doesnt-Have-Item-In-Hand")));
							return false;
						}
						Long price = Long.parseLong(args[1]);
						if(args[0].equalsIgnoreCase("Bid")){
							if(price<settings.getConfig().getLong("Settings.Minimum-Bid-Price")){
								player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Bid-Price-To-Low")));
								return true;
							}
							if(price>settings.getConfig().getLong("Settings.Max-Beginning-Bid-Price")){
								player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Bid-Price-To-High")));
								return true;
							}
						}else{
							if(price<settings.getConfig().getLong("Settings.Minimum-Sell-Price")){
								player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Sell-Price-To-Low")));
								return true;
							}
							if(price>settings.getConfig().getLong("Settings.Max-Beginning-Sell-Price")){
								player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Sell-Price-To-High")));
								return true;
							}
						}
						if(!player.hasPermission("crazyauctions.bypass")){
							int SellLimit = 0;
							int BidLimit = 0;
							for(PermissionAttachmentInfo permission : player.getEffectivePermissions()){
								String perm = permission.getPermission();
								if(perm.startsWith("crazyauctions.sell.")){
									perm=perm.replace("crazyauctions.sell.", "");
									if(Methods.isInt(perm)){
										if(Integer.parseInt(perm) > SellLimit){
											SellLimit = Integer.parseInt(perm);
										}
									}
								}
								if(perm.startsWith("crazyauctions.bid.")){
									perm=perm.replace("crazyauctions.bid.", "");
									if(Methods.isInt(perm)){
										if(Integer.parseInt(perm) > BidLimit){
											BidLimit = Integer.parseInt(perm);
										}
									}
								}
							}
							if(args[0].equalsIgnoreCase("Sell")){
								if(auc.getItems(player, Shop.SELL).size() >= SellLimit){
									player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Max-Items")));
									return true;
								}
							}
							if(args[0].equalsIgnoreCase("Bid")){
								if(auc.getItems(player, Shop.BID).size() >= BidLimit){
									player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Max-Items")));
									return true;
								}
							}
						}
						for(String id : settings.getConfig().getStringList("Settings.BlackList")){
							if(item.getType()==Methods.makeItem(id, 1).getType()){
								player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Item-BlackListed")));
								return true;
							}
						}

                                                // Blacklist by item lore
                                                if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                                                    for (String lore : settings.getConfig().getStringList("Settings.BlackListLore")) {
                                                        for (String ilore : item.getItemMeta().getLore()) {
                                                            if (ilore.matches(lore) || ilore.contains(lore)) {
                                                                player.sendMessage(Methods.getPrefix() + Methods.color(settings.getMsg().getString("Messages.Item-BlackListed")));
                                                                return true;
                                                            }
                                                        }
                                                    }
                                                }
                                                
						if(!settings.getConfig().getBoolean("Settings.Allow-Damaged-Items")){
							for(Material i : getDamageableItems()){
								if(item.getType()==i){
									if(item.getDurability()>0){
										player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Item-Damaged")));
										return true;
									}	
								}
							}
						}
						String seller = player.getName();
						int num = 1;
						Random r = new Random();
						for(;settings.getData().contains("Items."+num);num++);
						settings.getData().set("Items."+num+".Price", price);
						settings.getData().set("Items."+num+".Seller", seller);
						if(args[0].equalsIgnoreCase("Bid")){
							settings.getData().set("Items."+num+".Time-Till-Expire", Methods.convertToMill(settings.getConfig().getString("Settings.Bid-Time")));
						}else{
							settings.getData().set("Items."+num+".Time-Till-Expire", Methods.convertToMill(settings.getConfig().getString("Settings.Sell-Time")));
						}
						settings.getData().set("Items."+num+".Full-Time", Methods.convertToMill(settings.getConfig().getString("Settings.Full-Expire-Time")));
						int id = r.nextInt(999999);
						// Runs 3x to check for same ID.
						for(String i : settings.getData().getConfigurationSection("Items").getKeys(false))if(settings.getData().getInt("Items."+i+".StoreID")==id)id=r.nextInt(999999);
						for(String i : settings.getData().getConfigurationSection("Items").getKeys(false))if(settings.getData().getInt("Items."+i+".StoreID")==id)id=r.nextInt(999999);
						for(String i : settings.getData().getConfigurationSection("Items").getKeys(false))if(settings.getData().getInt("Items."+i+".StoreID")==id)id=r.nextInt(999999);
						settings.getData().set("Items."+num+".StoreID", id);
						if(args[0].equalsIgnoreCase("Bid")){
							settings.getData().set("Items."+num+".Biddable", true);
						}else{
							settings.getData().set("Items."+num+".Biddable", false);
						}
						settings.getData().set("Items."+num+".TopBidder", "None");
						ItemStack I = item.clone();
						I.setAmount(amount);
						settings.getData().set("Items."+num+".Item", I);
						settings.saveData();
						player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Added-Item-To-Auction")
								.replaceAll("%Price%", price+"").replaceAll("%price%", price+"")));
						if(item.getAmount()<=1||(item.getAmount()-amount)<=0){
							Methods.setItemInHand(player, new ItemStack(Material.AIR));
						}else{
							item.setAmount(item.getAmount()-amount);
						}
						return false;
					}
					sender.sendMessage(Methods.getPrefix()+Methods.color("/CA Sell/Bid <Price> [Amount of items]"));
					return true;
				}
			}
		}
		sender.sendMessage(Methods.getPrefix()+Methods.color("/CA Help"));
		return false;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		final Player player = e.getPlayer();
		new BukkitRunnable(){
			@Override
			public void run() {
				if(player.getName().equals("BadBones69")){
					player.sendMessage(Methods.getPrefix() + Methods.color("&7This server is running your Crazy Auctions Plugin. "
						+ "&7It is running version &av" + Bukkit.getServer().getPluginManager().getPlugin("CrazyAuctions").getDescription().getVersion() + "&7."));
				}
				if(player.isOp()){
					Methods.hasUpdate(player);
				}
			}
		}.runTaskLaterAsynchronously(this, 40);
	}
	
	private void startCheck(){
		new BukkitRunnable(){
			@Override
			public void run() {
				Methods.updateAuction();
			}
		}.runTaskTimer(this, 20, 5*20);
	}
	
	private ArrayList<Material> getDamageableItems(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.DIAMOND_CHESTPLATE);
		ma.add(Material.DIAMOND_LEGGINGS);
		ma.add(Material.DIAMOND_BOOTS);
		ma.add(Material.CHAINMAIL_HELMET);
		ma.add(Material.CHAINMAIL_CHESTPLATE);
		ma.add(Material.CHAINMAIL_LEGGINGS);
		ma.add(Material.CHAINMAIL_BOOTS);
		ma.add(Material.GOLD_HELMET);
		ma.add(Material.GOLD_CHESTPLATE);
		ma.add(Material.GOLD_LEGGINGS);
		ma.add(Material.GOLD_BOOTS);
		ma.add(Material.IRON_HELMET);
		ma.add(Material.IRON_CHESTPLATE);
		ma.add(Material.IRON_LEGGINGS);
		ma.add(Material.IRON_BOOTS);
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.DIAMOND_CHESTPLATE);
		ma.add(Material.DIAMOND_LEGGINGS);
		ma.add(Material.DIAMOND_BOOTS);
		ma.add(Material.BOW);
		ma.add(Material.WOOD_SWORD);
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(Material.DIAMOND_SWORD);
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.DIAMOND_AXE);
		ma.add(Material.WOOD_PICKAXE);
		ma.add(Material.STONE_PICKAXE);
		ma.add(Material.IRON_PICKAXE);
		ma.add(Material.DIAMOND_PICKAXE);
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.DIAMOND_AXE);
		ma.add(Material.WOOD_SPADE);
		ma.add(Material.STONE_SPADE);
		ma.add(Material.IRON_SPADE);
		ma.add(Material.DIAMOND_SPADE);
		ma.add(Material.WOOD_HOE);
		ma.add(Material.STONE_HOE);
		ma.add(Material.IRON_HOE);
		ma.add(Material.DIAMOND_HOE);
		ma.add(Material.FLINT_AND_STEEL);
		ma.add(Material.ANVIL);
		ma.add(Material.FISHING_ROD);
		return ma;
	}

}