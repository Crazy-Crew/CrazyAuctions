package com.badbones69.crazyauctions.utils;

import com.badbones69.crazyauctions.api.enums.ServerVersion;
import org.bukkit.Material;

public class ItemUtils {

    public static Material getMaterial(String newMaterial, String oldMaterial) {
        return Material.matchMaterial(ServerVersion.isAtLeast(ServerVersion.v1_12) ? newMaterial : oldMaterial);
    }
}