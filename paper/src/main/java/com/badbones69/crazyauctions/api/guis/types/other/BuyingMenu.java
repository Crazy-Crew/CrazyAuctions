package com.badbones69.crazyauctions.api.guis.types.other;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.api.events.AuctionBuyEvent;
import com.badbones69.crazyauctions.api.guis.Holder;
import com.badbones69.crazyauctions.api.guis.HolderManager;
import com.badbones69.crazyauctions.api.GuiManager;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.badbones69.crazyauctions.tasks.objects.AuctionItem;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Sound;
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
import java.util.UUID;

public class BuyingMenu extends Holder {

    private FileConfiguration config;
    private FileConfiguration data;

    private AuctionItem auction;
    private List<String> options;
    private String id;

    public BuyingMenu(final AuctionItem auction, final Player player, final String id, final String title) {
        super(player, title, 9);

        this.auction = auction;

        this.config = Files.config.getConfiguration();
        this.data = Files.data.getConfiguration();

        this.options = new ArrayList<>();

        this.id = id;
    }

    public BuyingMenu() {}

    @Override
    public final Holder build() {
        if (!this.data.contains("active_auctions." + this.auction.getUuid() + "." + this.id)) { // this grabs the uuid of the person who auctioned it
            GuiManager.openShop(this.player, ShopType.BID, HolderManager.getShopCategory(this.player), 1);

            this.player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(this.player));

            this.userManager.removeAuctionItem(this.auction); // remove auction item, as it's not in the active_auctions

            return this;
        }

        this.options.addAll(List.of(
                "Confirm",
                "Cancel"
        ));

        for (final String key : this.options) {
            if (!this.config.contains("Settings.GUISettings.OtherSettings." + key)) {
                continue;
            }

            if (!this.config.getBoolean("Settings.GUISettings.OtherSettings." + key + ".Toggle", true)) {
                continue;
            }

            final String id = this.config.getString("Settings.GUISettings.OtherSettings." + key + ".Item");
            final String name = this.config.getString("Settings.GUISettings.OtherSettings." + key + ".Name");

            final ItemBuilder itemBuilder = new ItemBuilder().setMaterial(id).setName(name).setAmount(1);

            if (this.config.contains("Settings.GUISettings.OtherSettings." + key + ".Lore")) {
                itemBuilder.setLore(this.config.getStringList("Settings.GUISettings.OtherSettings." + key + ".Lore")).addString(key, Keys.auction_button.getNamespacedKey());
            }

            switch (key) {
                case "Confirm" -> {
                    final ItemStack itemStack = itemBuilder.addString("Confirm", Keys.auction_button.getNamespacedKey()).build();

                    this.inventory.setItem(0, itemStack);
                    this.inventory.setItem(1, itemStack);
                    this.inventory.setItem(2, itemStack);
                    this.inventory.setItem(3, itemStack);
                }

                case "Cancel" -> {
                    final ItemStack itemStack = itemBuilder.addString("Cancel", Keys.auction_button.getNamespacedKey()).build();

                    this.inventory.setItem(5, itemStack);
                    this.inventory.setItem(6, itemStack);
                    this.inventory.setItem(7, itemStack);
                    this.inventory.setItem(8, itemStack);
                }
            }
        }

        this.inventory.setItem(4, this.auction.getActiveItem(ShopType.SELL).build());

        this.player.openInventory(this.inventory);

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof BuyingMenu buyingMenu)) return;

        event.setCancelled(true);

        final int slot = event.getSlot();

        final Inventory inventory = buyingMenu.getInventory();

        if (slot > inventory.getSize()) return;

        if (event.getCurrentItem() == null) return;

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) return;

        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        if (!container.has(Keys.auction_button.getNamespacedKey())) return;

        final String type = container.getOrDefault(Keys.auction_button.getNamespacedKey(), PersistentDataType.STRING, "");

        if (type.isEmpty()) return;

        final FileConfiguration data = buyingMenu.data;
        final Player player = buyingMenu.player;

        final AuctionItem auction = buyingMenu.auction;

        switch (type) {
            case "Confirm" -> {
                final UUID uuid = auction.getUuid();
                final String id = buyingMenu.id;

                if (!data.contains("active_auctions." + uuid + "." + id)) {
                    buyingMenu.click(player);

                    GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                    player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

                    this.userManager.removeAuctionItem(auction); // remove auction item, as it's not in the active_auctions

                    return;
                }

                if (Methods.isInvFull(player)) {
                    buyingMenu.click(player);

                    player.closeInventory();
                    player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

                    return;
                }

                final long cost = auction.getPrice();

                final VaultSupport support = this.plugin.getSupport();

                if (support.getMoney(player) < cost) {
                    buyingMenu.click(player);

                    player.closeInventory();

                    Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("%Money_Needed%", (cost - support.getMoney(player)) + "");
                    placeholders.put("%money_needed%", (cost - support.getMoney(player)) + "");

                    player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                    return;
                }

                final ItemStack item = auction.asItemStack();

                this.server.getPluginManager().callEvent(new AuctionBuyEvent(player, item, cost));
                support.removeMoney(player, cost);
                support.addMoney(Methods.getOfflinePlayer(String.valueOf(uuid)), cost);

                Map<String, String> placeholders = new HashMap<>();

                String price = String.valueOf(auction.getPrice());

                placeholders.put("%Price%", price);
                placeholders.put("%price%", price);
                placeholders.put("%Player%", player.getName());
                placeholders.put("%player%", player.getName());

                player.sendMessage(Messages.BOUGHT_ITEM.getMessage(player, placeholders));

                final Player originalSeller = Methods.getPlayer(String.valueOf(uuid));

                if (originalSeller != null && originalSeller.isOnline()) {
                    originalSeller.sendMessage(Messages.PLAYER_BOUGHT_ITEM.getMessage(player, placeholders));

                    FileConfiguration config = Files.config.getConfiguration();

                    String sound = config.getString("Settings.Sold-Item-Sound", "UI_BUTTON_CLICK");

                    if (sound.isEmpty()) return;

                    player.playSound(player.getLocation(), Sound.valueOf(sound), 1f, 1f);
                }

                player.getInventory().addItem(item);

                //data.set("active_auctions." + uuid + "." + id, null); // removeAuctionItem already handles this.

                this.userManager.removeAuctionItem(auction);

                Files.data.save();

                buyingMenu.click(player);

                GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);
            }

            case "Cancel" -> {
                GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                buyingMenu.click(player);
            }
        }
    }
}