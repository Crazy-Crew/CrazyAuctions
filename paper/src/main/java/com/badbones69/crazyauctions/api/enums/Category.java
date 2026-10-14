package com.badbones69.crazyauctions.api.enums;

import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NonNull;
import java.util.HashSet;
import java.util.Set;

public enum Category {
    
    NONE("None", Set.of()),
    ARMOR("Armor", Set.of(
            ItemType.CHAINMAIL_HELMET.key().asString(),
            ItemType.CHAINMAIL_CHESTPLATE.key().asString(),
            ItemType.CHAINMAIL_LEGGINGS.key().asString(),
            ItemType.CHAINMAIL_BOOTS.key().asString(),

            ItemType.IRON_HELMET.key().asString(),
            ItemType.IRON_CHESTPLATE.key().asString(),
            ItemType.IRON_LEGGINGS.key().asString(),
            ItemType.IRON_BOOTS.key().asString(),

            ItemType.DIAMOND_HELMET.key().asString(),
            ItemType.DIAMOND_CHESTPLATE.key().asString(),
            ItemType.DIAMOND_LEGGINGS.key().asString(),
            ItemType.DIAMOND_BOOTS.key().asString(),

            ItemType.GOLDEN_HELMET.key().asString(),
            ItemType.GOLDEN_CHESTPLATE.key().asString(),
            ItemType.GOLDEN_LEGGINGS.key().asString(),
            ItemType.GOLDEN_BOOTS.key().asString(),

            ItemType.NETHERITE_HELMET.key().asString(),
            ItemType.NETHERITE_CHESTPLATE.key().asString(),
            ItemType.NETHERITE_LEGGINGS.key().asString(),
            ItemType.NETHERITE_BOOTS.key().asString(),

            ItemType.TURTLE_HELMET.key().asString(),

            ItemType.COPPER_HELMET.key().asString(),
            ItemType.COPPER_CHESTPLATE.key().asString(),
            ItemType.COPPER_LEGGINGS.key().asString(),
            ItemType.COPPER_BOOTS.key().asString(),

            ItemType.LEATHER_HELMET.key().asString(),
            ItemType.LEATHER_CHESTPLATE.key().asString(),
            ItemType.LEATHER_LEGGINGS.key().asString(),
            ItemType.LEATHER_BOOTS.key().asString(),
            ItemType.LEATHER_HORSE_ARMOR.key().asString()
    )),
    WEAPONS("Weapons", Set.of(
            ItemType.WOODEN_SWORD.key().asString(),
            ItemType.STONE_SWORD.key().asString(),
            ItemType.IRON_SWORD.key().asString(),
            ItemType.DIAMOND_SWORD.key().asString(),
            ItemType.GOLDEN_SWORD.key().asString(),
            ItemType.NETHERITE_SWORD.key().asString(),
            ItemType.COPPER_SWORD.key().asString(),

            ItemType.WOODEN_AXE.key().asString(),
            ItemType.STONE_AXE.key().asString(),
            ItemType.IRON_AXE.key().asString(),
            ItemType.DIAMOND_AXE.key().asString(),
            ItemType.GOLDEN_AXE.key().asString(),
            ItemType.NETHERITE_AXE.key().asString(),
            ItemType.COPPER_AXE.key().asString(),

            ItemType.TRIDENT.key().asString(),
            ItemType.CROSSBOW.key().asString(),
            ItemType.BOW.key().asString()
    )),
    TOOLS("Tools", Set.of(
            ItemType.WOODEN_PICKAXE.key().asString(),
            ItemType.WOODEN_AXE.key().asString(),
            ItemType.WOODEN_HOE.key().asString(),
            ItemType.WOODEN_SHOVEL.key().asString(),

            ItemType.STONE_PICKAXE.key().asString(),
            ItemType.STONE_AXE.key().asString(),
            ItemType.STONE_HOE.key().asString(),
            ItemType.STONE_SHOVEL.key().asString(),

            ItemType.IRON_PICKAXE.key().asString(),
            ItemType.IRON_AXE.key().asString(),
            ItemType.IRON_HOE.key().asString(),
            ItemType.IRON_SHOVEL.key().asString(),

            ItemType.DIAMOND_PICKAXE.key().asString(),
            ItemType.DIAMOND_AXE.key().asString(),
            ItemType.DIAMOND_HOE.key().asString(),
            ItemType.DIAMOND_SHOVEL.key().asString(),

            ItemType.GOLDEN_PICKAXE.key().asString(),
            ItemType.GOLDEN_AXE.key().asString(),
            ItemType.GOLDEN_HOE.key().asString(),
            ItemType.GOLDEN_SHOVEL.key().asString(),

            ItemType.NETHERITE_PICKAXE.key().asString(),
            ItemType.NETHERITE_AXE.key().asString(),
            ItemType.NETHERITE_HOE.key().asString(),
            ItemType.NETHERITE_SHOVEL.key().asString(),

            ItemType.COPPER_SHOVEL.key().asString(),
            ItemType.COPPER_SWORD.key().asString(),
            ItemType.COPPER_AXE.key().asString(),
            ItemType.COPPER_HOE.key().asString()
    )),
    POTIONS("Potions", Set.of(
            ItemType.POTION.key().asString(),
            ItemType.SPLASH_POTION.key().asString(),
            ItemType.LINGERING_POTION.key().asString()
    )),
    FOOD("Food", getFood()),
    BLOCKS("Blocks", getBlocks()),
    OTHER("Other", getOthers());

    private final Set<String> items;
    private final String name;
    
    /**
     * @param name Name of the Shop Type.
     */
    Category(final String name, final Set<String> items) {
        this.items = items;
        this.name = name;
    }
    
    /**
     * @param name Name of the Type you want.
     * @return Returns the Type as an Enum.
     */
    public static Category getFromName(@NonNull final String name) {
        for (final Category type : Category.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
    
    private static Set<String> getFood() {
        final Set<String> types = new HashSet<>();

        for (final ItemType itemType : Registry.ITEM.stream().toList()) {
            final String asString = itemType.key().asString();

            if (Category.POTIONS.hasItem(asString)) continue;

            if (!itemType.isEdible()) continue;

            types.add(asString);
        }

        return types;
    }
    
    private static Set<String> getBlocks() {
        final Set<String> types = new HashSet<>();

        for (final ItemType itemType : Registry.ITEM.stream().toList()) {
            final String asString = itemType.key().asString();

            if (!itemType.hasBlockType()) continue;

            types.add(asString);
        }

        return types;
    }
    
    private static Set<String> getOthers() {
        final Set<String> types = new HashSet<>();

        for (final ItemType itemType : Registry.ITEM.stream().toList()) {
            final String asString = itemType.key().asString();

            types.add(asString);
        }

        return types;
    }

    public boolean hasItem(final String item) {
        if (this.items == null) {
            return false;
        }

        return this.items.contains(item);
    }

    public Set<String> getItems() {
        return this.items;
    }

    public String getName() {
        return this.name;
    }
}