package me.badbones69.crazyauctions.api;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazyauctions.Main;

public class CrazyAuctions {
	
	public static CrazyAuctions instance;
	
	public static CrazyAuctions getInstance() {
		if(instance == null){
			instance  = new CrazyAuctions();
		}
		return instance;
	}
	
	public ArrayList<ItemStack> getItems(Player player){
		FileConfiguration data = Main.settings.getData();
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if(data.contains("Items")){
			for(String i : data.getConfigurationSection("Items").getKeys(false)){
				if(data.getString("Items." + i + ".Seller").equalsIgnoreCase(player.getName())){
					items.add(data.getItemStack("Items."+i+".Item").clone());
				}
			}
		}
		return items;
	}
	
	public ArrayList<ItemStack> getItems(Player player, ShopType type){
		FileConfiguration data = Main.settings.getData();
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if(data.contains("Items")){
			for(String i : data.getConfigurationSection("Items").getKeys(false)){
				if(data.getString("Items." + i + ".Seller").equalsIgnoreCase(player.getName())){
					if(data.getBoolean("Items." + i + ".Biddable")){
						if(type == ShopType.BID){
							items.add(data.getItemStack("Items."+i+".Item").clone());
						}
					}else{
						if(type == ShopType.SELL){
							items.add(data.getItemStack("Items."+i+".Item").clone());
						}
					}
				}
			}
		}
		return items;
	}
	
}