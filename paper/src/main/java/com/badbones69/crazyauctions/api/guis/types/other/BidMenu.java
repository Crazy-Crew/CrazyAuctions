package com.badbones69.crazyauctions.api.guis.types.other;

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
import com.badbones69.crazyauctions.tasks.objects.AuctionItem;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BidMenu extends Holder {

    private FileConfiguration config;
    private FileConfiguration data;

    private AuctionItem auction;
    private String id;

    public BidMenu(final AuctionItem auction, final Player player, final String id, final String title) {
        super(player, title, 27);

        this.auction = auction;

        this.config = Files.config.getConfiguration();
        this.data = Files.data.getConfiguration();

        this.id = id;
    }

    public BidMenu() {}

    @Override
    public final Holder build() {
        final UUID uuid = this.player.getUniqueId();

        if (!this.data.contains("active_auctions." + uuid + "." + this.id)) {
            GuiManager.openShop(this.player, ShopType.BID, HolderManager.getShopCategory(this.player), 1);

            this.player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(this.player));

            this.userManager.removeAuctionItem(this.auction); // remove auction item, as it's not in the active_auctions

            return this;
        }

        final ItemStack item = this.auction.getActiveItem(ShopType.BID).getItemStack();

        final ItemBuilder builder = new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setAmount(1);

        final NamespacedKey auction_button = Keys.auction_button.getNamespacedKey();
        final NamespacedKey auction_price = Keys.auction_price.getNamespacedKey();

        this.inventory.setItem(9, builder.addString("+1", auction_button).addInteger(1, auction_price).setName("&a+1").build());
        this.inventory.setItem(10, builder.addString("+10", auction_button).addInteger(10, auction_price).setName("&a+10").build());
        this.inventory.setItem(11, builder.addString("+100", auction_button).addInteger(100, auction_price).setName("&a+100").build());
        this.inventory.setItem(12, builder.addString("+1000", auction_button).addInteger(1000, auction_price).setName("&a+1000").build());
        this.inventory.setItem(14, builder.addString("-1000", auction_button).addInteger(-1000, auction_price).setName("&c-1000").build());
        this.inventory.setItem(15, builder.addString("-100", auction_button).addInteger(-100, auction_price).setName("&c-100").build());
        this.inventory.setItem(16, builder.addString("-10", auction_button).addInteger(-10, auction_price).setName("&c-10").build());
        this.inventory.setItem(17, builder.addString("-1", auction_button).addInteger(-1, auction_price).setName("&c-1").build());

        this.inventory.setItem(13, getGlass(null));

        this.inventory.setItem(22, new ItemBuilder().addString("bid_item", auction_button).setMaterial(this.config.getString("Settings.GUISettings.OtherSettings.Bid.Item")).setAmount(1)
                .setName(this.config.getString("Settings.GUISettings.OtherSettings.Bid.Name")).setLore(this.config.getStringList("Settings.GUISettings.OtherSettings.Bid.Lore")).build());

        this.inventory.setItem(4, item);

        this.player.openInventory(this.inventory);

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof BidMenu bidMenu)) return;

        event.setCancelled(true);

        final int slot = event.getSlot();

        final Inventory inventory = bidMenu.getInventory();

        if (slot > inventory.getSize()) return;

        if (event.getCurrentItem() == null) return;

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) return;

        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        if (!container.has(Keys.auction_button.getNamespacedKey())) return;

        final String type = container.getOrDefault(Keys.auction_button.getNamespacedKey(), PersistentDataType.STRING, "");

        if (type.isEmpty()) return;

        final Player player = bidMenu.player;

        final AuctionItem auction = bidMenu.auction;

        switch (type) {
            case "bid_item" -> {
                final VaultSupport support = this.plugin.getSupport();

                final long bid = auction.getTopBid();

                final long money = support.getMoney(player);

                final String topBidder = auction.getBidderName();

                if (money < bid) {
                    final Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("%Money_Needed%", String.valueOf(bid - money));
                    placeholders.put("%money_needed%", String.valueOf(bid - money));

                    player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                    return;
                }

                if (auction.getPrice() > bid) {
                    player.sendMessage(Messages.BID_MORE_MONEY.getMessage(player));

                    return;
                }

                if (auction.getPrice() >= bid && !topBidder.equalsIgnoreCase("None")) {
                    player.sendMessage(Messages.BID_MORE_MONEY.getMessage(player));

                    return;
                }

                this.server.getPluginManager().callEvent(new AuctionNewBidEvent(player, auction.asItemStack(), bid));

                final Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%Bid%", String.valueOf(bid));

                player.sendMessage(Messages.BID_MESSAGE.getMessage(player, placeholders));

                auction.setBidderName(player.getName());
                auction.setBidderUUID(player.getUniqueId().toString());
                auction.setPrice(bid);

                this.userManager.removeAuctionItem(auction); // remove it

                final FileConfiguration data = bidMenu.data;

                final String uuid = auction.getUuid().toString();

                final ConfigurationSection section = data.getConfigurationSection("active_auctions." + uuid + "." + bidMenu.id);

                if (section == null) { // do not add if not found in data.yml
                    player.closeInventory();

                    bidMenu.click(player);

                    return;
                }

                this.userManager.addActiveAuction(auction.getUuid().toString(), section);

                player.closeInventory();
                bidMenu.click(player);
            }

            case "+1", "+10", "+100", "+1000" -> {
                final int price = container.getOrDefault(Keys.auction_price.getNamespacedKey(), PersistentDataType.INTEGER, 10);

                auction.setTopBid(auction.getTopBid() + price);

                this.inventory.setItem(4, auction.getActiveItem(ShopType.BID).getItemStack());
                this.inventory.setItem(13, getGlass(bidMenu));
            }

            case "-1", "-10", "-100", "-1000" -> {
                final int price = container.getOrDefault(Keys.auction_price.getNamespacedKey(), PersistentDataType.INTEGER, -10);

                auction.setTopBid(auction.getTopBid() + price);

                this.inventory.setItem(4, auction.getActiveItem(ShopType.BID).getItemStack());
                this.inventory.setItem(13, getGlass(bidMenu));
            }
        }
    }

    private ItemStack getGlass(@Nullable final BidMenu bidMenu) {
        FileConfiguration config = bidMenu != null ? bidMenu.config : this.config;

        String item = config.getString("Settings.GUISettings.OtherSettings.Bidding.Item");
        String name = config.getString("Settings.GUISettings.OtherSettings.Bidding.Name");

        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(item).setName(name).setAmount(1);

        final AuctionItem auction = bidMenu != null ? bidMenu.auction : this.auction;

        final long bid = auction.getPrice();
        final long topBid = auction.getTopBid();

        if (config.contains("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
            List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

            lore.add(" ");

            for (String line : config.getStringList("Settings.GUISettings.OtherSettings.Bidding.Lore")) {
                lore.add(line.replace("%Bid%", String.valueOf(bid))
                        .replace("%bid%", String.valueOf(bid))
                        .replace("%TopBid%", String.valueOf(topBid))
                        .replace("%topbid%", String.valueOf(topBid)));
            }

            itemBuilder.setLore(lore);
        }

        return itemBuilder.build();
    }
}