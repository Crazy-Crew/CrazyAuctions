package com.badbones69.crazyauctions.api.users;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.FileManager.Files;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserManager implements IUserManager {

    private final @NotNull CrazyAuctions plugin = CrazyAuctions.get();

    @Override
    public void migrate() {
        FileConfiguration users = Files.DATA.getFile();
        
        if (users.getString("Items") == null) {
            this.plugin.getLogger().warning("users.yml is already converted so we do nothing!");
            return;
        }

        ConfigurationSection section = users.getConfigurationSection("Items");

        if (section != null) section.getKeys(false).forEach(number -> {
            String path = "Items." + number;

            String playerName = users.getString(path + ".Seller");

            int price = users.getInt(path + ".Price");
            long timeUntilExpire = users.getLong(path + ".Time-Till-Expire");
            long fullTime = users.getLong(path + ".Full-Time");
            int storeID = users.getInt(path + ".StoreID");
            boolean canBid = users.getBoolean(path + ".Biddable");
            String topBidder = users.getString(path + ".TopBidder", "None");

            ItemStack itemStack = users.getItemStack(path + ".Item");

            Player player = this.plugin.getServer().getPlayer(playerName);

            UUID uuid = player != null ? player.getUniqueId() : CompletableFuture.supplyAsync(() -> plugin.getServer().getOfflinePlayer(playerName)).thenApply(OfflinePlayer::getUniqueId).join();

            String newPath = "active-items." + uuid + "." + number;

            users.set(newPath + ".price", price);
            users.set(newPath + ".time-till-expire", timeUntilExpire);
            users.set(newPath + ".full-time", fullTime);
            users.set(newPath + ".store-id", storeID);
            users.set(newPath + ".biddable", canBid);

            if (!topBidder.equalsIgnoreCase("None")) {
                Player other = this.plugin.getServer().getPlayer(topBidder);

                UUID topBid = other != null ? other.getUniqueId() : CompletableFuture.supplyAsync(() -> plugin.getServer().getOfflinePlayer(topBidder)).thenApply(OfflinePlayer::getUniqueId).join();

                users.set(newPath + ".top-bidder", topBid.toString());
            } else {
                users.set(newPath + ".top-bidder", topBidder);
            }

            users.set(newPath + ".item", itemStack);

            Files.DATA.saveFile();
        });

        ConfigurationSection cancelled = users.getConfigurationSection("OutOfTime/Cancelled");

        if (cancelled != null) {
            cancelled.getKeys(false).forEach(number -> {
                String path = "OutOfTime/Cancelled." + number;

                String playerName = users.getString(path + ".Seller");

                long fullTime = users.getLong(path + ".Full-Time");
                int storeID = users.getInt(path + ".StoreID");

                ItemStack itemStack = users.getItemStack(path + ".Item");

                Player player = this.plugin.getServer().getPlayer(playerName);

                UUID uuid = player != null ? player.getUniqueId() : CompletableFuture.supplyAsync(() -> plugin.getServer().getOfflinePlayer(playerName)).thenApply(OfflinePlayer::getUniqueId).join();

                String newPath = "expired-items." + uuid + "." + number;

                users.set(newPath + ".full-time", fullTime);
                users.set(newPath + ".store-id", storeID);
                users.set(newPath + ".item", itemStack);

                Files.DATA.saveFile();
            });
        }

        users.set("Items", null);
        users.set("OutOfTime/Cancelled", null);

        Files.DATA.saveFile();
    }

    @Override
    public void auction(Player player, ItemStack itemStack, Long price, Long auctionTime, Long fullTime, boolean isBiddable) {
        FileConfiguration users = Files.DATA.getFile();
        
        UUID uuid = player.getUniqueId();

        int number = 1;

        Random random = new Random();

        String path = "active-items." + uuid + ".";

        for (;users.contains(path + number); number++);

        users.set(path + number + ".price", price);
        users.set(path + number + ".time-till-expire", auctionTime);

        users.set(path + number + ".full-time", fullTime);

        int id = random.nextInt(999999);

        // Why is this here?
        for (String amount : users.getConfigurationSection(path + number).getKeys(false)) {
            int value = users.getInt(path + amount + ".store-id");

            if (value == id) id = random.nextInt(Integer.MAX_VALUE);
        }

        users.set(path + number + ".store-id", id);

        users.set(path + number + ".biddable", isBiddable);

        users.set(path + number + ".top-bidder", "None");

        users.set(path + number + ".item", itemStack);

        Files.DATA.saveFile();
    }

    @Override
    public List<ItemStack> activeItems(Player player) {
        FileConfiguration users = Files.DATA.getFile();
        FileConfiguration config = Files.CONFIG.getFile();

        List<ItemStack> items = new ArrayList<>();

        UUID uuid = player.getUniqueId();

        String path = "active-items." + uuid;

        for (String id : users.getConfigurationSection(path).getKeys(false)) {
            List<String> lore = new ArrayList<>();

            config.getStringList("Settings.GUISettings.CurrentLore").forEach(line -> {
                String newLine = line.replace("%price%", Methods.getPrice(player, id, false))
                        .replace("%time%", Methods.convertToTime(users.getLong(path + "." + id + ".time-till-expire")));

                lore.add(newLine);
            });

            items.add(Methods.addLore(users.getItemStack(path + "." + id + ".item"), lore));
        }

        return items;
    }

    @Override
    public List<Integer> activeIds(Player player) {
        FileConfiguration users = Files.DATA.getFile();

        List<Integer> ids = new ArrayList<>();

        UUID uuid = player.getUniqueId();

        String path = "active-items." + uuid;

        for (String id : users.getConfigurationSection(path).getKeys(false)) {
            ids.add(users.getInt(path + "." + id + ".store-id"));
        }

        return ids;
    }

    @Override
    public List<ItemStack> expiredItems(Player player) {
        FileConfiguration users = Files.DATA.getFile();
        FileConfiguration config = Files.CONFIG.getFile();

        List<ItemStack> items = new ArrayList<>();

        UUID uuid = player.getUniqueId();

        String path = "expired-items." + uuid;

        for (String id : users.getConfigurationSection(path).getKeys(false)) {
            List<String> lore = new ArrayList<>();

            config.getStringList("Settings.GUISettings.Cancelled/ExpiredLore").forEach(line -> {
                String newLine = line.replace("%price%", Methods.getPrice(player, id, true))
                        .replace("%time%", Methods.convertToTime(users.getLong(path + "." + id + ".full-time")));

                lore.add(newLine);
            });

            items.add(Methods.addLore(users.getItemStack(path + "." + id + ".item"), lore));
        }

        return items;
    }

    @Override
    public List<Integer> expiredIds(Player player) {
        FileConfiguration users = Files.DATA.getFile();

        List<Integer> ids = new ArrayList<>();

        UUID uuid = player.getUniqueId();

        String path = "expired-items." + uuid;

        for (String id : users.getConfigurationSection(path).getKeys(false)) {
            ids.add(users.getInt(path + "." + id + ".store-id"));
        }

        return ids;
    }
}