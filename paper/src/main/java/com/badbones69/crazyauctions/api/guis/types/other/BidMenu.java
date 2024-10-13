package com.badbones69.crazyauctions.api.guis.types.other;

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
import org.bukkit.NamespacedKey;
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

        NamespacedKey auction_button = Keys.auction_button.getNamespacedKey();
        NamespacedKey auction_price = Keys.auction_price.getNamespacedKey();

        this.inventory.setItem(9, builder.addString("+1", auction_button).addInteger(1, auction_price).setName("&a+1").build());
        this.inventory.setItem(10, builder.addString("+10", auction_button).addInteger(10, auction_price).setName("&a+10").build());
        this.inventory.setItem(11, builder.addString("+100", auction_button).addInteger(100, auction_price).setName("&a+100").build());
        this.inventory.setItem(12, builder.addString("+1000", auction_button).addInteger(1000, auction_price).setName("&a+1000").build());
        this.inventory.setItem(14, builder.addString("-1000", auction_button).addInteger(-1000, auction_price).setName("&c-1000").build());
        this.inventory.setItem(15, builder.addString("-100", auction_button).addInteger(-100, auction_price).setName("&c-100").build());
        this.inventory.setItem(16, builder.addString("-10", auction_button).addInteger(-10, auction_price).setName("&c-10").build());
        this.inventory.setItem(17, builder.addString("-1", auction_button).addInteger(-1, auction_price).setName("&c-1").build());
        this.inventory.setItem(13, getBiddingGlass(this.player, this.id));

        this.inventory.setItem(22, new ItemBuilder().addString("bid_item", auction_button).setMaterial(this.config.getString("Settings.GUISettings.OtherSettings.Bid.Item")).setAmount(1)
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
                String id = menu.id;

                int bid = HolderManager.getBidding(player);

                String topBidder = data.getString("Items." + id + ".TopBidder", "None");

                final long money = support.getMoney(player);

                if (money < bid) {
                    final Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("%Money_Needed%", String.valueOf(bid - money));
                    placeholders.put("%money_needed%", String.valueOf(bid - money));

                    player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                    return;
                }

                if (data.getLong("Items." + id + ".Price") > bid) {
                    player.sendMessage(Messages.BID_MORE_MONEY.getMessage(player));

                    return;
                }

                if (data.getLong("Items." + id + ".Price") >= bid && !topBidder.equalsIgnoreCase("None")) {
                    player.sendMessage(Messages.BID_MORE_MONEY.getMessage(player));

                    return;
                }

                this.server.getPluginManager().callEvent(new AuctionNewBidEvent(player, Methods.fromBase64(data.getString("Items." + id + ".Item")), bid));

                data.set("Items." + id + ".Price", bid);
                data.set("Items." + id + ".TopBidder", player.getUniqueId().toString());
                data.set("Items." + id + ".TopBidderName", player.getName());

                final Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%Bid%", String.valueOf(bid));

                player.sendMessage(Messages.BID_MESSAGE.getMessage(player, placeholders));

                Files.data.save();

                HolderManager.addBidding(player, 0);
                player.closeInventory();
                menu.click(player);
            }

            case "+1", "+10", "+100", "+1000" -> {
                try {
                    final int price = container.getOrDefault(Keys.auction_price.getNamespacedKey(), PersistentDataType.INTEGER, 10);

                    HolderManager.addBidding(player, HolderManager.getBidding(player) + price);

                    this.inventory.setItem(4, getBiddingItem(menu.id));
                    this.inventory.setItem(13, getBiddingGlass(player, menu.id));
                } catch (Exception exception) {
                    player.closeInventory();

                    player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));
                }
            }

            case "-1", "-10", "-100", "-1000" -> {
                try {
                    final int price = container.getOrDefault(Keys.auction_price.getNamespacedKey(), PersistentDataType.INTEGER, -10);

                    HolderManager.addBidding(player, HolderManager.getBidding(player) + price);

                    this.inventory.setItem(4, getBiddingItem(menu.id));
                    this.inventory.setItem(13, getBiddingGlass(player, menu.id));
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

        String seller = data.getString("Items." + id + ".Name", "None");
        String bidder = data.getString("Items." + id + ".TopBidderName", "None");

        for (String l : config.getStringList("Settings.GUISettings.Bidding")) {
            lore.add(l.replace("%TopBid%", price)
                    .replace("%topbid%", price)
                    .replace("%Seller%", seller).replace("%seller%", seller)
                    .replace("%TopBidder%", bidder).replace("%topbidder%", bidder)
                    .replace("%Time%", time)
                    .replace("%time%", time));
        }

        itemBuilder.setLore(lore);

        return itemBuilder.build();
    }
}