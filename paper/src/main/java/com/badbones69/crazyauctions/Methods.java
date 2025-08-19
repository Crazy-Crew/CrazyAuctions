package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.Reasons;
import com.badbones69.crazyauctions.api.events.AuctionCancelledEvent;
import com.badbones69.crazyauctions.api.events.AuctionExpireEvent;
import com.badbones69.crazyauctions.api.events.AuctionWinBidEvent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Methods {

    private final static CrazyAuctions plugin = CrazyAuctions.get();

    private final static Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String color(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
    
    public static String getPrefix(String msg) {
        return color(Files.config.getConfiguration().getString("Settings.Prefix", "") + msg);
    }

    public static ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }
    
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public static @Nullable Player getPlayer(String uuid) {
        return plugin.getServer().getPlayer(UUID.fromString(uuid));
    }

    public static String toBase64(final ItemStack itemStack) {
        return Base64.getEncoder().encodeToString(itemStack.serializeAsBytes());
    }

    public static @NotNull ItemStack fromBase64(final String base64) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(base64));
    }

    public static OfflinePlayer getOfflinePlayer(String uuid) {
        return plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));
    }

    public static boolean isOnline(String uuid) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getUniqueId().toString().equals(uuid)) {
                return true;
            }
        }

        return false;
    }
    
    public static List<ItemStack> getPage(List<ItemStack> list, Integer page) {
        List<ItemStack> items = new ArrayList<>();

        if (page <= 0) page = 1;

        int max = 45;
        int index = page * max - max;
        int endIndex = index >= list.size() ? list.size() - 1 : index + max;

        for (; index < endIndex; index++) {
            if (index < list.size()) items.add(list.get(index));
        }

        for (; items.size() == 0; page--) {
            if (page <= 0) break;
            index = page * max - max;
            endIndex = index >= list.size() ? list.size() - 1 : index + max;
            for (; index < endIndex; index++) {
                if (index < list.size()) items.add(list.get(index));
            }
        }

        return items;
    }
    
    public static List<Integer> getPageInts(List<Integer> list, Integer page) {
        List<Integer> items = new ArrayList<>();

        if (page <= 0) page = 1;

        int max = 45;
        int index = page * max - max;
        int endIndex = index >= list.size() ? list.size() - 1 : index + max;

        for (; index < endIndex; index++) {
            if (index < list.size()) items.add(list.get(index));
        }

        for (; items.isEmpty(); page--) {
            if (page <= 0) break;

            index = page * max - max;
            endIndex = index >= list.size() ? list.size() - 1 : index + max;

            for (; index < endIndex; index++) {
                if (index < list.size()) items.add(list.get(index));
            }
        }

        return items;
    }
    
    public static int getMaxPage(List<ItemStack> list) {
        int maxPage = 1;
        int amount = list.size();

        for (; amount > 45; amount -= 45, maxPage++) ;

        return maxPage;
    }
    
    public static String convertToTime(long time) {
        Calendar C = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(time);

        int total = ((int) (cal.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
        int D = 0;
        int H = 0;
        int M = 0;
        int S = 0;

        for (; total > 86400; total -= 86400, D++) ;
        for (; total > 3600; total -= 3600, H++) ;
        for (; total > 60; total -= 60, M++) ;

        S += total;

        return D + "d " + H + "h " + M + "m " + S + "s ";
    }
    
    public static long convertToMill(String time) {
        Calendar cal = Calendar.getInstance();

        for (String i : time.split(" ")) {
            if (i.contains("D") || i.contains("d")) {
                cal.add(Calendar.DATE, Integer.parseInt(i.replace("D", "").replace("d", "")));
            }

            if (i.contains("H") || i.contains("h")) {
                cal.add(Calendar.HOUR, Integer.parseInt(i.replace("H", "").replace("h", "")));
            }

            if (i.contains("M") || i.contains("m")) {
                cal.add(Calendar.MINUTE, Integer.parseInt(i.replace("M", "").replace("m", "")));
            }

            if (i.contains("S") || i.contains("s")) {
                cal.add(Calendar.SECOND, Integer.parseInt(i.replace("S", "").replace("s", "")));
            }
        }

        return cal.getTimeInMillis();
    }
    
    public static boolean isInvFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
    
    public static void updateAuction() {
        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        Calendar cal = Calendar.getInstance();
        Calendar expireTime = Calendar.getInstance();
        Calendar fullExpireTime = Calendar.getInstance();

        boolean shouldSave = false;

        if (data.contains("OutOfTime/Cancelled")) {
            for (String i : data.getConfigurationSection("OutOfTime/Cancelled").getKeys(false)) {
                fullExpireTime.setTimeInMillis(data.getLong("OutOfTime/Cancelled." + i + ".Full-Time"));

                if (cal.after(fullExpireTime)) {
                    data.set("OutOfTime/Cancelled." + i, null);

                    shouldSave = true;
                }
            }
        }

        if (data.contains("Items")) {
            for (String i : data.getConfigurationSection("Items").getKeys(false)) {
                expireTime.setTimeInMillis(data.getLong("Items." + i + ".Time-Till-Expire"));
                fullExpireTime.setTimeInMillis(data.getLong("Items." + i + ".Full-Time"));

                if (cal.after(expireTime)) {
                    int num = 1;

                    for (; data.contains("OutOfTime/Cancelled." + num); num++) ;

                    if (data.getBoolean("Items." + i + ".Biddable") && !data.getString("Items." + i + ".TopBidder").equalsIgnoreCase("None") && plugin.getSupport().getMoney(getOfflinePlayer(data.getString("Items." + i + ".TopBidder"))) >= data.getInt("Items." + i + ".Price")) {
                        String winner = data.getString("Items." + i + ".TopBidder");
                        String seller = data.getString("Items." + i + ".Seller");
                        long price = data.getLong("Items." + i + ".Price");
                        long taxAmount = (long) (price * config.getDouble("Settings.Percent-Tax", 0) / 100);
                        long taxedPriceAmount = Math.max(price - taxAmount, 0);

                        OfflinePlayer sellerPlayer = Methods.getOfflinePlayer(seller);
                        OfflinePlayer winnerPlayer = Methods.getOfflinePlayer(winner);

                        plugin.getSupport().addMoney(sellerPlayer, price);
                        plugin.getSupport().removeMoney(winnerPlayer, price);

                        String tax = String.valueOf(taxAmount);
                        String taxedPrice = String.valueOf(taxedPriceAmount);

                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Price%", getPrice(i, false));
                        placeholders.put("%price%", getPrice(i, false));
                        placeholders.put("%Tax%", tax);
                        placeholders.put("%tax%", tax);
                        placeholders.put("%Taxed_Price%", taxedPrice);
                        placeholders.put("%taxed_price%", taxedPrice);
                        placeholders.put("%Player%", winnerPlayer.getName());
                        placeholders.put("%player%", winnerPlayer.getName());
                        placeholders.put("%Seller%", sellerPlayer.getName());
                        placeholders.put("%seller%", sellerPlayer.getName());

                        if (isOnline(winner) && getPlayer(winner) != null) {
                            Player player = getPlayer(winner);

                            new AuctionWinBidEvent(player, Methods.fromBase64(data.getString("Items." + i + ".Item")), price).callEvent();

                            if (player != null) {
                                player.sendMessage(Messages.WIN_BIDDING.getMessage(player, placeholders));
                            }
                        }

                        if (isOnline(seller) && getPlayer(seller) != null) {
                            Player player = getPlayer(seller);

                            if (player != null) {
                                player.sendMessage(Messages.SOMEONE_WON_PLAYERS_BID.getMessage(player, placeholders));
                            }
                        }

                        data.set("OutOfTime/Cancelled." + num + ".Seller", winner);
                        data.set("OutOfTime/Cancelled." + num + ".Full-Time", fullExpireTime.getTimeInMillis());
                        data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
                        data.set("OutOfTime/Cancelled." + num + ".Item", data.getString("Items." + i + ".Item"));
                    } else {
                        String seller = data.getString("Items." + i + ".Seller");
                        Player player = getPlayer(seller);

                        if (isOnline(seller) && player != null) {
                            player.sendMessage(Messages.ITEM_HAS_EXPIRED.getMessage(player));
                        }

                        AuctionExpireEvent event = new AuctionExpireEvent(player, Methods.fromBase64(data.getString("Items." + i + ".Item")));
                        event.callEvent();

                        data.set("OutOfTime/Cancelled." + num + ".Seller", data.getString("Items." + i + ".Seller"));
                        data.set("OutOfTime/Cancelled." + num + ".Full-Time", fullExpireTime.getTimeInMillis());
                        data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
                        data.set("OutOfTime/Cancelled." + num + ".Item", data.getString("Items." + i + ".Item"));
                    }

                    data.set("Items." + i, null);
                    shouldSave = true;
                }
            }
        }

        if (shouldSave) Files.data.save();
    }
    
    public static String getPrice(String ID, Boolean Expired) {
        long price = 0L;

        FileConfiguration configuration = Files.data.getConfiguration();

        if (Expired) {
            if (configuration.contains("OutOfTime/Cancelled." + ID + ".Price")) {
                price = configuration.getLong("OutOfTime/Cancelled." + ID + ".Price");
            }
        } else {
            if (configuration.contains("Items." + ID + ".Price")) {
                price = configuration.getLong("Items." + ID + ".Price");
            }
        }

        return String.valueOf(price);
    }

    /**
     * Moves an item from the auction house to the expired section.
     *
     * @param num The section the item is saved to under expired items.
     * @param seller The {@link OfflinePlayer} that sold the item.
     * @param i The section where the listed item was saved.
     * @param data The file in which the data is saved.
     * @return The section in which the item was saved.
     */
    public static int expireItem(int num, OfflinePlayer seller, String i, FileConfiguration data, Reasons reasons) {

        while (data.contains("OutOfTime/Cancelled." + num)) num++;

        AuctionCancelledEvent event = new AuctionCancelledEvent(seller, Methods.fromBase64(data.getString("Items." + i + ".Item")), reasons);
        event.callEvent();

        data.set("OutOfTime/Cancelled." + num + ".Seller", data.getString("Items." + i + ".Seller"));
        data.set("OutOfTime/Cancelled." + num + ".Full-Time", data.getLong("Items." + i + ".Full-Time"));
        data.set("OutOfTime/Cancelled." + num + ".StoreID", data.getInt("Items." + i + ".StoreID"));
        data.set("OutOfTime/Cancelled." + num + ".Item", data.getString("Items." + i + ".Item"));

        data.set("Items." + i, null);

        return num;
    }

}