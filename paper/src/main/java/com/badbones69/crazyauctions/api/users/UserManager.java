package com.badbones69.crazyauctions.api.users;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.FileManager.Files;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserManager implements IUserManager {

    private final @NotNull CrazyAuctions plugin = CrazyAuctions.get();

    private final @NotNull FileConfiguration users = Files.DATA.getFile();

    @Override
    public void migrate() {
        if (this.users.getString("Items") == null) {
            this.plugin.getLogger().warning("users.yml is already converted so we do nothing!");
            return;
        }

        ConfigurationSection section = this.users.getConfigurationSection("Items");

        if (section != null) section.getKeys(false).forEach(number -> {
            String path = "Items." + number;

            String playerName = this.users.getString(path + ".Seller");

            int price = this.users.getInt(path + ".Price");
            long timeUntilExpire = this.users.getLong(path + ".Time-Till-Expire");
            long fullTime = this.users.getLong(path + ".Full-Time");
            int storeID = this.users.getInt(path + ".StoreID");
            boolean canBid = this.users.getBoolean(path + ".Biddable");
            String topBidder = this.users.getString(path + ".TopBidder", "None");

            ItemStack itemStack = this.users.getItemStack(path + ".Item");

            Player player = this.plugin.getServer().getPlayer(playerName);

            UUID uuid = player != null ? player.getUniqueId() : CompletableFuture.supplyAsync(() -> plugin.getServer().getOfflinePlayer(playerName)).thenApply(OfflinePlayer::getUniqueId).join();

            String newPath = "active-items." + uuid + "." + number;

            this.users.set(newPath + ".price", price);
            this.users.set(newPath + ".time-till-expire", timeUntilExpire);
            this.users.set(newPath + ".full-time", fullTime);
            this.users.set(newPath + ".store-id", storeID);
            this.users.set(newPath + ".biddable", canBid);

            if (!topBidder.equalsIgnoreCase("None")) {
                Player other = this.plugin.getServer().getPlayer(topBidder);

                UUID topBid = other != null ? other.getUniqueId() : CompletableFuture.supplyAsync(() -> plugin.getServer().getOfflinePlayer(topBidder)).thenApply(OfflinePlayer::getUniqueId).join();

                this.users.set(newPath + ".top-bidder", topBid.toString());
            } else {
                this.users.set(newPath + ".top-bidder", topBidder);
            }

            this.users.set(newPath + ".item", itemStack);

            Files.DATA.saveFile();
        });

        ConfigurationSection cancelled = this.users.getConfigurationSection("OutOfTime/Cancelled");

        if (cancelled != null) {
            cancelled.getKeys(false).forEach(number -> {
                String path = "OutOfTime/Cancelled." + number;

                String playerName = this.users.getString(path + ".Seller");

                long fullTime = this.users.getLong(path + ".Full-Time");
                int storeID = this.users.getInt(path + ".StoreID");

                ItemStack itemStack = this.users.getItemStack(path + ".Item");

                Player player = this.plugin.getServer().getPlayer(playerName);

                UUID uuid = player != null ? player.getUniqueId() : CompletableFuture.supplyAsync(() -> plugin.getServer().getOfflinePlayer(playerName)).thenApply(OfflinePlayer::getUniqueId).join();

                String newPath = "expired-items." + uuid + "." + number;

                this.users.set(newPath + ".full-time", fullTime);
                this.users.set(newPath + ".store-id", storeID);
                this.users.set(newPath + ".item", itemStack);

                Files.DATA.saveFile();
            });
        }

        this.users.set("Items", null);
        this.users.set("OutOfTime/Cancelled", null);

        Files.DATA.saveFile();
    }

    @Override
    public void save(UUID uuid) {

    }
}