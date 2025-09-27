package com.badbones69.crazyauctions.controllers;

import org.bukkit.Material;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.enums.Files;
import com.badbones69.crazyauctions.api.events.AuctionListEvent;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.currency.EconomySession;
import com.badbones69.crazyauctions.currency.EconomySessionFactory;
import com.badbones69.crazyauctions.currency.CoinsEngineSupport;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ItemSeller {
    public static boolean sell(EconomySession session, Player player, ItemStack item, int amount, double price, boolean isBid) {
        return sellItems(session, player, item, amount, price, isBid, "");
    }

    public static boolean sell(EconomySession session, Player player, ItemStack item, int amount, double price, boolean isBid, String currency) {
        return sellItems(session, player, item, amount, price, isBid, currency);
    }

    public static boolean sellItems(EconomySession session, Player player, ItemStack item, int amount, double price, boolean isBid, String currency) {
        FileConfiguration data = Files.data.getConfiguration();
        FileConfiguration config = Files.config.getConfiguration();

        double listPercent = config.getDouble("Settings.Auction-List-Percent-Tax", (double) 0);
        double listCost = (double) (price * listPercent) / 100;

        if (session.getMoney(player) <= listCost) {
            Map<String, String> placeholders = new HashMap<>() {{
                put("%Money_Needed%", String.valueOf(listCost) + " " + CoinsEngineSupport.getCurrencySymbol(currency));
                put("%money_needed%", String.valueOf(listCost) + " " + CoinsEngineSupport.getCurrencySymbol(currency));
            }};
            player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));
            return false;
        }
        session.removeMoney(player, listCost);

        int num = 1;
        while (data.contains("Items." + num)) num++;

        data.set("Items." + num + ".Price", price);
        data.set("Items." + num + ".Seller", player.getUniqueId().toString());
        data.set("Items." + num + ".SellerName", player.getName());
        data.set("Items." + num + ".Currency", currency);

        if (isBid) {
            data.set("Items." + num + ".Time-Till-Expire", Methods.convertToMill(config.getString("Settings.Bid-Time", "2m 30s")));
            data.set("Items." + num + ".Biddable", true);
        } else {
            data.set("Items." + num + ".Time-Till-Expire", Methods.convertToMill(config.getString("Settings.Sell-Time", "2d")));
            data.set("Items." + num + ".Biddable", false);
        }

        data.set("Items." + num + ".Full-Time", Methods.convertToMill(config.getString("Settings.Full-Expire-Time", "10d")));
        int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        // Runs 3x to check for same ID.
        for (String i : data.getConfigurationSection("Items").getKeys(false))
            if (data.getInt("Items." + i + ".StoreID") == id)
                id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        for (String i : data.getConfigurationSection("Items").getKeys(false))
            if (data.getInt("Items." + i + ".StoreID") == id)
                id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        for (String i : data.getConfigurationSection("Items").getKeys(false))
            if (data.getInt("Items." + i + ".StoreID") == id)
                id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);

        data.set("Items." + num + ".StoreID", id);

        ItemStack stack = item.clone();
        stack.setAmount(amount);

        data.set("Items." + num + ".Item", Methods.toBase64(stack));

        Files.data.save();

        new AuctionListEvent(player, isBid ? ShopType.BID : ShopType.SELL, stack, price, currency).callEvent();

        Map<String, String> placeholders = new HashMap<>();

        String priceWithCurrency = GuiListener.formatPriceWithCurrency(config, data, String.valueOf(num), String.valueOf(price));
        String listCostWithCurrency = GuiListener.formatPriceWithCurrency(config, data, String.valueOf(num), String.valueOf(listCost));
        placeholders.put("%Price%", priceWithCurrency);
        placeholders.put("%price%", priceWithCurrency);
        placeholders.put("%listpercent%", String.valueOf(listPercent));
        placeholders.put("%ListPercent%", String.valueOf(listPercent));
        placeholders.put("%ListCost%", listCostWithCurrency);
        placeholders.put("%listcost%", listCostWithCurrency);

        player.sendMessage(Messages.ADDED_ITEM_TO_AUCTION.getMessage(player, placeholders));

        if (item.getAmount() <= 1 || (item.getAmount() - amount) <= 0) {
            Methods.setItemInHand(player, new ItemStack(Material.AIR));
        } else {
            item.setAmount(item.getAmount() - amount);
        }
        return true;
    }
}
