package com.badbones69.crazyauctions.api.users;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public interface IUserManager {

    void migrate();

    void auction(Player player, ItemStack itemStack, Long price, Long auctionTime, Long fullTime, boolean isBiddable);

    List<ItemStack> activeItems(Player player);

    List<Integer> activeIds(Player player);

    List<ItemStack> expiredItems(Player player);

    List<Integer> expiredIds(Player player);

}