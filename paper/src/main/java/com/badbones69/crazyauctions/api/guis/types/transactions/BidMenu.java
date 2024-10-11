package com.badbones69.crazyauctions.api.guis.types.transactions;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.api.events.AuctionNewBidEvent;
import com.badbones69.crazyauctions.api.guis.Holder;
import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.api.GuiManager;
import com.badbones69.crazyauctions.currency.VaultSupport;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BidMenu extends Holder {

    private FileConfiguration config;
    private FileConfiguration data;

    private String id;

    public BidMenu(final Player player, final String id, final String title) {
        super(player, title, 27);

        this.config = Files.config.getConfiguration();
        this.data = Files.data.getConfiguration();

        this.id = id;
    }

    public BidMenu() {}

    @Override
    public final Holder build() {
        Methods.updateAuction();

        if (!this.data.contains("Items." + this.id)) {
            GuiManager.openShop(this.player, ShopType.BID, HolderManager.getShopCategory(this.player), 1);

            this.player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(this.player));

            return this;
        }

        if (!HolderManager.containsBidding(this.player)) HolderManager.addBidding(this.player, 0);

        final ItemBuilder builder = new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setAmount(1);

        this.inventory.setItem(9, builder.addString("+1").addInteger(1).setName("&a+1").build());
        this.inventory.setItem(10, builder.addString("+10").addInteger(10).setName("&a+10").build());
        this.inventory.setItem(11, builder.addString("+100").addInteger(100).setName("&a+100").build());
        this.inventory.setItem(12, builder.addString("+1000").addInteger(1000).setName("&a+1000").build());
        this.inventory.setItem(14, builder.addString("-1000").addInteger(-1000).setName("&c-1000").build());
        this.inventory.setItem(15, builder.addString("-100").addInteger(-100).setName("&c-100").build());
        this.inventory.setItem(16, builder.addString("-10").addInteger(-10).setName("&c-10").build());
        this.inventory.setItem(17, builder.addString("-1").addInteger(-1).setName("&c-1").build());
        this.inventory.setItem(13, getBiddingGlass(this.player, this.id));

        this.inventory.setItem(22, new ItemBuilder().addString("bid_item").setMaterial(this.config.getString("Settings.GUISettings.OtherSettings.Bid.Item")).setAmount(1)
                .setName(this.config.getString("Settings.GUISettings.OtherSettings.Bid.Name")).setLore(this.config.getStringList("Settings.GUISettings.OtherSettings.Bid.Lore")).build());

        this.inventory.setItem(4, getBiddingItem(this.id));

        this.player.openInventory(this.inventory);

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof BidMenu menu)) return;

        event.setCancelled(true);

        final int slot = event.getSlot();

        final Inventory inventory = menu.getInventory();

        if (slot > inventory.getSize()) return;

        if (event.getCurrentItem() == null) return;

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) return;

        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        if (!container.has(Keys.auction_button.getNamespacedKey())) return;

        final String type = container.getOrDefault(Keys.auction_button.getNamespacedKey(), PersistentDataType.STRING, "");

        if (type.isEmpty()) return;

        final FileConfiguration data = Files.data.getConfiguration();
        final Player player = (Player) event.getWhoClicked();
        final VaultSupport support = this.plugin.getSupport();

        switch (type) {
            case "bid_item" -> {
                String ID = HolderManager.getBidId(player);

                int bid = HolderManager.getBidding(player);

                String topBidder = data.getString("Items." + ID + ".TopBidder", "None");

                if (support.getMoney(player) < bid) {
                    Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("%Money_Needed%", (bid - support.getMoney(player)) + "");
                    placeholders.put("%money_needed%", (bid - support.getMoney(player)) + "");

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

                this.server.getPluginManager().callEvent(new AuctionNewBidEvent(player, Methods.fromBase64(data.getString("Items." + ID + ".Item")), bid));

                data.set("Items." + ID + ".Price", bid);
                data.set("Items." + ID + ".TopBidder", player.getUniqueId().toString());

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%Bid%", bid + "");

                player.sendMessage(Messages.BID_MESSAGE.getMessage(player, placeholders));

                Files.data.save();

                HolderManager.addBidding(player, 0);
                player.closeInventory();
                click();
            }

            case "+1", "+10", "+100", "+1000" -> {
                try {
                    final int price = container.getOrDefault(Keys.auction_price.getNamespacedKey(), PersistentDataType.INTEGER, 10);

                    HolderManager.addBidding(player, HolderManager.getBidding(player) + price);

                    final String bid_id = HolderManager.getBidId(player);

                    this.inventory.setItem(4, getBiddingItem(bid_id));
                    this.inventory.setItem(13, getBiddingGlass(player, bid_id));
                } catch (Exception exception) {
                    player.closeInventory();

                    player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));
                }
            }

            case "-1", "-10", "-100", "-1000" -> {
                try {
                    final int price = container.getOrDefault(Keys.auction_price.getNamespacedKey(), PersistentDataType.INTEGER, -10);

                    HolderManager.addBidding(player, HolderManager.getBidding(player) + price);

                    final String bid_id = HolderManager.getBidId(player);

                    this.inventory.setItem(4, getBiddingItem(bid_id));
                    this.inventory.setItem(13, getBiddingGlass(player, bid_id));
                } catch (Exception exception) {
                    player.closeInventory();

                    player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));
                }
            }
        }
    }

    private ItemStack getBiddingGlass(Player player, String id) {
        FileConfiguration config = Files.config.getConfiguration();

        String item = config.getString("Settings.GUISettings.OtherSettings.Bidding.Item");
        String name = config.getString("Settings.GUISettings.OtherSettings.Bidding.Name");

        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(item).setName(name).setAmount(1);

        int bid = HolderManager.getBidding(player);

        String price = Methods.getPrice(id, false);

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

    private ItemStack getBiddingItem(final String id) {
        FileConfiguration config = Files.config.getConfiguration();
        FileConfiguration data = Files.data.getConfiguration();

        ItemStack item = Methods.fromBase64(data.getString("Items." + id + ".Item"));

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(item);

        List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

        lore.add(" ");

        String price = Methods.getPrice(id, false);
        String time = Methods.convertToTime(data.getLong("Items." + id + ".Time-Till-Expire"));

        OfflinePlayer target = null;

        String seller = data.getString("Items." + id + ".Seller");

        if (seller != null) {
            target = Methods.getOfflinePlayer(seller);
        }

        OfflinePlayer bidder = null;

        String bid = data.getString("Items." + id + ".TopBidder");

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
}