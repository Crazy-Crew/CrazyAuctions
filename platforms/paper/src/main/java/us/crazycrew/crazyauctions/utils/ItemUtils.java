package us.crazycrew.crazyauctions.utils;

import us.crazycrew.crazyauctions.api.enums.ServerVersion;
import org.bukkit.Material;
import us.crazycrew.crazycore.paper.items.ItemBuilder;
import java.util.Arrays;

public class ItemUtils {

    public static Material getMaterial(String newMaterial, String oldMaterial) {
        return Material.matchMaterial(ServerVersion.isAtLeast(ServerVersion.v1_12) ? newMaterial : oldMaterial);
    }

    /**
     * Converts a string to an ItemBuilder with a placeholder for errors.
     *
     * @param itemString The String you wish to convert.
     * @return The String as an ItemBuilder.
     */
    public static ItemBuilder convertString(String itemString) {
        ItemBuilder itemBuilder = new ItemBuilder();

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item":
                        itemBuilder.setValue(value);
                        break;
                    case "name":
                        //itemBuilder.setDisplayName(value);
                        break;
                    case "amount":
                        try {
                            itemBuilder.setAmount(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setAmount(1);
                        }
                        break;
                    case "lore":
                        //itemBuilder.setLore(Arrays.asList(value.split(",")));
                        break;
                    case "player":
                        itemBuilder.setTexture(value);
                        break;
                    case "unbreakable-item":
                        //if (value.isEmpty() || value.equalsIgnoreCase("true")) itemBuilder.setUnbreakable(true);
                        break;
                    default:
//                        Enchantment enchantment = getEnchantment(option);
//
//                        if (enchantment != null && enchantment.getName() != null) {
//                            try {
//                                itemBuilder.addEnchantments(enchantment, Integer.parseInt(value));
//                            } catch (NumberFormatException e) {
//                                itemBuilder.addEnchantments(enchantment, 1);
//                            }
//
//                            break;
//                        }
//
//                        for (ItemFlag itemFlag : ItemFlag.values()) {
//                            if (itemFlag.name().equalsIgnoreCase(option)) {
//                                itemBuilder.addItemFlags(Arrays.asList(itemFlag.name()));
//                                break;
//                            }
//                        }
//
//                        try {
//                            for (PatternType pattern : PatternType.values()) {
//                                if (option.equalsIgnoreCase(pattern.name()) || value.equalsIgnoreCase(pattern.getIdentifier())) {
//                                    DyeColor color = getDyeColor(value);
//                                    if (color != null) itemBuilder.addPattern(new Pattern(color, pattern));
//                                    break;
//                                }
//                            }
//                        } catch (Exception ignored) {
//                        }
                        break;
                }
            }
        } catch (Exception e) {
            //ItemBuilder.setMaterial(Material.RED_TERRACOTTA).setDisplayName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor : &c"));
            e.printStackTrace();
        }

        return itemBuilder;
    }
}