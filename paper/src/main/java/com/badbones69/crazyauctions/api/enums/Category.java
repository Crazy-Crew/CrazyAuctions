package com.badbones69.crazyauctions.api.enums;

import org.bukkit.Material;
import java.util.ArrayList;

public enum Category {
    
    NONE("None", new ArrayList<>()),
    OTHER("Other", getOthers()),
    ARMOR("Armor", getArmor()),
    WEAPONS("Weapons", getWeapons()),
    TOOLS("Tools", getTools()),
    FOOD("Food", getFood()),
    POTIONS("Potions", getPotions()),
    BLOCKS("Blocks", getBlocks());
    
    private final String name;
    private final ArrayList<Material> items;
    
    /**
     * @param name Name of the Shop Type.
     */
    Category(String name, ArrayList<Material> items) {
        this.name = name;
        this.items = items;
    }
    
    /**
     * @param name Name of the Type you want.
     * @return Returns the Type as an Enum.
     */
    public static Category getFromName(String name) {
        for (Category type : Category.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
    
    private static ArrayList<Material> getArmor() {
        ArrayList<Material> ma = new ArrayList<>();

        ma.add(Material.GOLDEN_HELMET);
        ma.add(Material.GOLDEN_CHESTPLATE);
        ma.add(Material.GOLDEN_LEGGINGS);
        ma.add(Material.GOLDEN_BOOTS);
        ma.add(Material.DIAMOND_HELMET);
        ma.add(Material.DIAMOND_CHESTPLATE);
        ma.add(Material.DIAMOND_LEGGINGS);
        ma.add(Material.DIAMOND_BOOTS);
        ma.add(Material.CHAINMAIL_HELMET);
        ma.add(Material.CHAINMAIL_CHESTPLATE);
        ma.add(Material.CHAINMAIL_LEGGINGS);
        ma.add(Material.CHAINMAIL_BOOTS);
        ma.add(Material.IRON_HELMET);
        ma.add(Material.IRON_CHESTPLATE);
        ma.add(Material.IRON_LEGGINGS);
        ma.add(Material.IRON_BOOTS);
        ma.add(Material.DIAMOND_HELMET);
        ma.add(Material.DIAMOND_CHESTPLATE);
        ma.add(Material.DIAMOND_LEGGINGS);
        ma.add(Material.DIAMOND_BOOTS);
        ma.add(Material.NETHERITE_HELMET);
        ma.add(Material.NETHERITE_CHESTPLATE);
        ma.add(Material.NETHERITE_LEGGINGS);
        ma.add(Material.NETHERITE_BOOTS);

        return ma;
    }
    
    private static ArrayList<Material> getTools() {
        ArrayList<Material> ma = new ArrayList<>();

        ma.add(Material.WOODEN_PICKAXE);
        ma.add(Material.WOODEN_AXE);
        ma.add(Material.WOODEN_SHOVEL);
        ma.add(Material.WOODEN_HOE);
        ma.add(Material.GOLDEN_PICKAXE);
        ma.add(Material.GOLDEN_AXE);
        ma.add(Material.GOLDEN_SHOVEL);
        ma.add(Material.GOLDEN_HOE);
        ma.add(Material.STONE_SHOVEL);
        ma.add(Material.IRON_SHOVEL);
        ma.add(Material.DIAMOND_SHOVEL);
        ma.add(Material.STONE_PICKAXE);
        ma.add(Material.IRON_PICKAXE);
        ma.add(Material.DIAMOND_PICKAXE);
        ma.add(Material.STONE_AXE);
        ma.add(Material.IRON_AXE);
        ma.add(Material.DIAMOND_AXE);
        ma.add(Material.STONE_HOE);
        ma.add(Material.IRON_HOE);
        ma.add(Material.DIAMOND_HOE);
        ma.add(Material.NETHERITE_PICKAXE);
        ma.add(Material.NETHERITE_AXE);
        ma.add(Material.NETHERITE_HOE);
        ma.add(Material.NETHERITE_SHOVEL);

        return ma;
    }
    
    private static ArrayList<Material> getWeapons() {
        ArrayList<Material> ma = new ArrayList<>();

        ma.add(Material.GOLDEN_PICKAXE);
        ma.add(Material.GOLDEN_AXE);
        ma.add(Material.GOLDEN_SHOVEL);
        ma.add(Material.GOLDEN_HOE);
        ma.add(Material.STONE_SWORD);
        ma.add(Material.IRON_SWORD);
        ma.add(Material.DIAMOND_SWORD);
        ma.add(Material.STONE_AXE);
        ma.add(Material.IRON_AXE);
        ma.add(Material.DIAMOND_AXE);
        ma.add(Material.BOW);
        ma.add(Material.NETHERITE_SWORD);
        ma.add(Material.NETHERITE_AXE);

        ma.add(Material.MACE);

        return ma;
    }
    
    private static ArrayList<Material> getFood() {
        ArrayList<Material> ma = new ArrayList<>();

        for (Material m : Material.values()) {
            if (m.isEdible()) {
                if (m != Material.POTION) ma.add(m);
            }
        }

        return ma;
    }
    
    private static ArrayList<Material> getPotions() {
        ArrayList<Material> ma = new ArrayList<>();

        ma.add(Material.POTION);
        ma.add(Material.SPLASH_POTION);
        ma.add(Material.LINGERING_POTION);

        return ma;
    }
    
    private static ArrayList<Material> getBlocks() {
        ArrayList<Material> ma = new ArrayList<>();

        for (Material m : Material.values()) {
            if (m.isBlock()) {
                ma.add(m);
            }
        }

        return ma;
    }
    
    private static ArrayList<Material> getOthers() {
        ArrayList<Material> ma = new ArrayList<>();

        for (Material m : Material.values()) {
            if (!(getArmor().contains(m) || getTools().contains(m) || getWeapons().contains(m) || getFood().contains(m) || getPotions().contains(m) || getBlocks().contains(m))) {
                ma.add(m);
            }
        }

        return ma;
    }
    
    /**
     * @return Returns the type name as a string.
     */
    public String getName() {
        return this.name;
    }
    
    public ArrayList<Material> getItems() {
        return this.items;
    }
}