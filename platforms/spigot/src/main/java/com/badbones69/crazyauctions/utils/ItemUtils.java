package com.badbones69.crazyauctions.utils;

import com.badbones69.crazyauctions.api.enums.ServerProtocol;
import org.bukkit.Material;

public class ItemUtils {

    public static Material getMaterial(String newMaterial, String oldMaterial) {
        return Material.matchMaterial(ServerProtocol.isNewer(ServerProtocol.v1_12_R1) ? newMaterial : oldMaterial);
    }
}