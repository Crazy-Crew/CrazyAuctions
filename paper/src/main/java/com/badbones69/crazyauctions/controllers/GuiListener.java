package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.events.AuctionNewBidEvent;
import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.api.guis.types.AuctionsMenu;
import com.badbones69.crazyauctions.api.guis.types.CategoriesMenu;
import com.badbones69.crazyauctions.api.guis.types.CurrentMenu;
import com.badbones69.crazyauctions.api.guis.types.ExpiredMenu;
import com.badbones69.crazyauctions.api.guis.types.transactions.BuyingMenu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiListener implements Listener {

    private static final CrazyAuctions plugin = CrazyAuctions.getPlugin();

    public static void openShop(Player player, ShopType shopType, Category category, int page) {
        FileConfiguration config = Files.config.getConfiguration();

        new AuctionsMenu(player, shopType, category, config.getString("Settings.GUIName", "N/A"), 54, page);
    }

    public static void openCategories(Player player, ShopType shopType) {
        FileConfiguration config = Files.config.getConfiguration();

        new CategoriesMenu(player, shopType, config.getString("Settings.Categories", "N/A"), 54).build();
    }

    public static void openPlayersCurrentList(Player player, int page) {
        FileConfiguration config = Files.config.getConfiguration();

        new CurrentMenu(player, config.getString("Settings.Players-Current-Items", "N/A"), 54, page);
    }

    public static void openPlayersExpiredList(Player player, int page) {
        FileConfiguration config = Files.config.getConfiguration();

        new ExpiredMenu(player, config.getString("Settings.Cancelled/Expired-Items") + " #" + page, 54, page).build();
    }

    public static void openBuying(Player player, String ID) {
        final FileConfiguration config = Files.config.getConfiguration();

        new BuyingMenu(player, ID, config.getString("Settings.Buying-Item", "N/A"), 9).build();
    }

    public static void openBidding(Player player, String ID) {
        Methods.updateAuction();

        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        if (!data.contains("Items." + ID)) {
            openShop(player, ShopType.BID, HolderManager.getShopCategory(player), 1);

            player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

            return;
        }

        Inventory inv = plugin.getServer().createInventory(null, 27, Methods.color(config.getString("Settings.Bidding-On-Item")));

        if (!HolderManager.containsBidding(player)) HolderManager.addBidding(player, 0);

        final ItemBuilder builder = new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setAmount(1);

        inv.setItem(9, builder.setName("&a+1").build());
        inv.setItem(10, builder.setName("&a+10").build());
        inv.setItem(11, builder.setName("&a+100").build());
        inv.setItem(12, builder.setName("&a+1000").build());
        inv.setItem(14, builder.setName("&c-1000").build());
        inv.setItem(15, builder.setName("&c-100").build());
        inv.setItem(16, builder.setName("&c-10").build());
        inv.setItem(17, builder.setName("&c-1").build());
        inv.setItem(13, getBiddingGlass(player, ID));

        inv.setItem(22, new ItemBuilder().setMaterial(config.getString("Settings.GUISettings.OtherSettings.Bid.Item")).setAmount(1)
                .setName(config.getString("Settings.GUISettings.OtherSettings.Bid.Name")).setLore(config.getStringList("Settings.GUISettings.OtherSettings.Bid.Lore")).build());

        inv.setItem(4, getBiddingItem(ID));

        player.openInventory(inv);
    }

    public static void openViewer(Player player, String other, int page) {
        FileConfiguration config = Files.config.getConfiguration();

        new AuctionsMenu(player, other, config.getString("Settings.GUIName") + " #" + page, 54, page).build();
    }

    private static ItemStack getBiddingGlass(Player player, String ID) {
        FileConfiguration config = Files.config.getConfiguration();

        String id = config.getString("Settings.GUISettings.OtherSettings.Bidding.Item");
        String name = config.getString("Settings.GUISettings.OtherSettings.Bidding.Name");

        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

        int bid = HolderManager.getBidding(player);

        String price = Methods.getPrice(ID, false);

        if (config.contains("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
            List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

            lore.add(" ");

            for (String l : config.getStringList("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
                lore.add(l.replace("%Bid%", String.valueOf(bid))
                        .replace("%bid%", String.valueOf(bid))
                        .replace("%TopBid%", price)
                        .replace("%topbid%", price));
            }

            itemBuilder.setLore(lore);
        }

        return itemBuilder.build();
    }

    private static ItemStack getBiddingItem(String ID) {
        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        ItemStack item = Methods.fromBase64(data.getString("Items." + ID + ".Item"));

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(item);

        List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

        lore.add(" ");

        String price = Methods.getPrice(ID, false);
        String time = Methods.convertToTime(data.getLong("Items." + ID + ".Time-Till-Expire"));

        OfflinePlayer target = null;

        String id = data.getString("Items." + ID + ".Seller");

        if (id != null) {
            target = Methods.getOfflinePlayer(id);
        }

        OfflinePlayer bidder = null;

        String bid = data.getString("Items." + ID + ".TopBidder");

        if (bid != null && !bid.equals("None")) {
            bidder = Methods.getOfflinePlayer(bid);
        }

        for (String l : config.getStringList("Settings.GUISettings.Bidding")) {
            lore.add(l.replace("%TopBid%", price)
                    .replace("%topbid%", price)
                    .replace("%Seller%", target != null ? target.getName() : "N/A").replace("%seller%", target != null ? target.getName() : "N/A")
                    .replace("%TopBidder%", bidder != null ? bidder.getName() : "N/A").replace("%topbidder%", bidder != null ? bidder.getName() : "N/A")
                    .replace("%Time%", time)
                    .replace("%time%", time));
        }

        itemBuilder.setLore(lore);

        return itemBuilder.build();
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        Player player = (Player) e.getWhoClicked();
        final Inventory inv = e.getClickedInventory();

        if (inv == null) return;

        final ItemStack item = e.getCurrentItem();

        if (item == null) return;

        if (!item.hasItemMeta()) return;

        final ItemMeta itemMeta = item.getItemMeta();

        if (!itemMeta.hasDisplayName()) return;

        final InventoryView view = e.getView();

        final String title = view.getTitle();

        final String displayName = itemMeta.getDisplayName();

        final String strippedTitle = Methods.strip(title);

        final String strippedDisplayName = Methods.strip(displayName);

        if (strippedTitle.contains(Methods.strip(config.getString("Settings.Bidding-On-Item")))) {
            e.setCancelled(true);
            int slot = e.getRawSlot();

            if (slot > inv.getSize()) return;

            if (strippedDisplayName.equalsIgnoreCase(Methods.strip(config.getString("Settings.GUISettings.OtherSettings.Bid.Name")))) {
                String ID = HolderManager.getBidId(player);
                int bid = HolderManager.getBidding(player);
                String topBidder = data.getString("Items." + ID + ".TopBidder");

                if (plugin.getSupport().getMoney(player) < bid) {
                    Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("%Money_Needed%", (bid - plugin.getSupport().getMoney(player)) + "");
                    placeholders.put("%money_needed%", (bid - plugin.getSupport().getMoney(player)) + "");

                    player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                    return;
                }

                if (data.getLong("Items." + ID + ".Price") > bid) {
                    player.sendMessage(Messages.BID_MORE_MONEY.getMessage(player));

                    return;
                }

                if (data.getLong("Items." + ID + ".Price") >= bid && !topBidder.equalsIgnoreCase("None")) {
                    player.sendMessage(Messages.BID_MORE_MONEY.getMessage(player));

                    return;
                }

                plugin.getServer().getPluginManager().callEvent(new AuctionNewBidEvent(player, Methods.fromBase64(data.getString("Items." + ID + ".Item")), bid));

                data.set("Items." + ID + ".Price", bid);
                data.set("Items." + ID + ".TopBidder", player.getUniqueId().toString());

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%Bid%", bid + "");

                player.sendMessage(Messages.BID_MESSAGE.getMessage(player, placeholders));

                Files.data.save();

                HolderManager.addBidding(player, 0);
                player.closeInventory();
                playClick(player);
                return;
            }

            Map<String, Integer> priceEdits = new HashMap<>();
            priceEdits.put("&a+1", 1);
            priceEdits.put("&a+10", 10);
            priceEdits.put("&a+100", 100);
            priceEdits.put("&a+1000", 1000);
            priceEdits.put("&c-1", -1);
            priceEdits.put("&c-10", -10);
            priceEdits.put("&c-100", -100);
            priceEdits.put("&c-1000", -1000);

            for (String price : priceEdits.keySet()) {
                if (strippedDisplayName.equals(Methods.strip(price))) {
                    try {
                        HolderManager.addBidding(player, HolderManager.getBidding(player) + priceEdits.get(price));

                        inv.setItem(4, getBiddingItem(HolderManager.getBidId(player)));

                        inv.setItem(13, getBiddingGlass(player, HolderManager.getBidId(player)));

                        playClick(player);

                        return;
                    } catch (Exception ex) {
                        player.closeInventory();

                        player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

                        return;
                    }
                }
            }
        }
    }

    private static void playClick(Player player) {
        FileConfiguration config = Files.config.getConfiguration();

        if (config.getBoolean("Settings.Sounds.Toggle", false)) {
            String sound = config.getString("Settings.Sounds.Sound");

            try {
                player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 1);
            } catch (Exception e) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1F);
            }
        }
    }
}