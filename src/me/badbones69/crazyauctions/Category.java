package me.badbones69.crazyauctions;

import java.util.ArrayList;

import org.bukkit.Material;

public enum Category{
	
	NONE("None", new ArrayList<Material>()),
	OTHER("Other", getOthers()),
	ARMOR("Armor", getArmor()),
	WEAPONS("Weapons", getWeapons()),
	TOOLS("Tools", getTools()),
	FOOD("Food", getFood()),
	POTIONS("Potions", getPotions()),
	BLOCKS("Blocks", getBlocks());
	
	private String Name;
	private ArrayList<Material> Items;
	
	
	/**
	 * @param name Name of the Shop Type.
	 */
	private Category(String name, ArrayList<Material> items){
		this.Name = name;
		this.Items = items;
	}
	
	/**
	 * @return Returns the type name as a string.
	 */
	public String getName(){
		return Name;
	}
	
	/**
	 * @param name Name of the Type you want.
	 * @return Returns the Type as a Enum.
	 */
	public static Category getFromName(String name){
		for(Category type : Category.values()){
			if(type.getName().equalsIgnoreCase(name)){
				return type;
			}
		}
		return null;
	}
	
	public ArrayList<Material> getItems(){
		return Items;
	}
	
	private static ArrayList<Material> getArmor(){
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
		return ma;
	}
	
	private static ArrayList<Material> getTools(){
		ArrayList<Material> ma = new ArrayList<Material>();
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
		return ma;
	}
	
	private static ArrayList<Material> getWeapons(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_SWORD);
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(Material.DIAMOND_SWORD);
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.DIAMOND_AXE);
		ma.add(Material.BOW);
		return ma;
	}

	private static ArrayList<Material> getFood(){
		ArrayList<Material> ma = new ArrayList<Material>();
		for(Material m : Material.values()){
			if(m.isEdible()){
				if(m!=Material.POTION)ma.add(m);
			}
		}
		return ma;
	}

	private static ArrayList<Material> getPotions(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.POTION);
		return ma;
	}

	private static ArrayList<Material> getBlocks(){
		ArrayList<Material> ma = new ArrayList<Material>();
		for(Material m : Material.values()){
			if(m.isBlock()){
				ma.add(m);
			}
		}
		return ma;
	}

	private static ArrayList<Material> getOthers(){
		ArrayList<Material> ma = new ArrayList<Material>();
		for(Material m : Material.values()){
			if(!(getArmor().contains(m)||getTools().contains(m)||getWeapons().contains(m)||
					getFood().contains(m)||getPotions().contains(m)||getBlocks().contains(m))){
				ma.add(m);
			}
		}
		return ma;
	}

}