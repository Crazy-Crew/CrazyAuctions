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
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.OfflinePlayer;
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

public class BuyingMenu extends Holder {

    private FileConfiguration config;
    private FileConfiguration data;

    private List<String> options;
    private String id;

    public BuyingMenu(final Player player, final String id, final String title) {
        super(player, title, 9);

        this.config = Files.config.getConfiguration();
        this.data = Files.data.getConfiguration();

        this.options = new ArrayList<>();

        this.id = id;
    }

    public BuyingMenu() {}

    @Override
    public final Holder build() {
        Methods.updateAuction();

        if (!this.data.contains("Items." + this.id)) {
            GuiManager.openShop(this.player, ShopType.SELL, HolderManager.getShopCategory(this.player), 1);

            this.player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(this.player));

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

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(this.data.getString("Items." + this.id + ".Item"));

        List<String> lore = new ArrayList<>(itemBuilder.getUpdatedLore());

        lore.add(" ");

        String price = Methods.getPrice(this.id, false);
        String time = Methods.convertToTime(this.data.getLong("Items." + this.id + ".Time-Till-Expire"));

        OfflinePlayer target = null;

        String id = this.data.getString("Items." + this.id + ".Seller");

        if (id != null) {
            target = Methods.getOfflinePlayer(id);
        }

        for (String l : config.getStringList("Settings.GUISettings.SellingItemLore")) {
            lore.add(l.replace("%Price%", price).replace("%price%", price)
                    .replace("%Seller%", target != null ? target.getName() : "N/A")
                    .replace("%seller%", target != null ? target.getName() : "N/A")
                    .replace("%Time%", time)
                    .replace("%time%", time));
        }

        itemBuilder.setLore(lore);

        this.inventory.setItem(4, itemBuilder.build());

        HolderManager.addId(this.player, this.id);

        this.player.openInventory(this.inventory);

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof BuyingMenu menu)) return;

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

        switch (type) {
            case "Confirm" -> {
                String ID = HolderManager.getId(player);
                long cost = data.getLong("Items." + ID + ".Price");
                String seller = data.getString("Items." + ID + ".Seller");

                if (!data.contains("Items." + ID)) {
                    menu.click(player);

                    GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                    player.sendMessage(Messages.ITEM_DOESNT_EXIST.getMessage(player));

                    return;
                }

                if (Methods.isInvFull(player)) {
                    menu.click(player);

                    player.closeInventory();
                    player.sendMessage(Messages.INVENTORY_FULL.getMessage(player));

                    return;
                }

                final VaultSupport support = this.plugin.getSupport();

                if (support.getMoney(player) < cost) {
                    menu.click(player);
                    player.closeInventory();

                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Money_Needed%", (cost - support.getMoney(player)) + "");
                    placeholders.put("%money_needed%", (cost - support.getMoney(player)) + "");

                    player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, placeholders));

                    return;
                }

                ItemStack i = Methods.fromBase64(data.getString("Items." + ID + ".Item"));

                this.server.getPluginManager().callEvent(new AuctionBuyEvent(player, i, cost));
                support.removeMoney(player, cost);
                support.addMoney(Methods.getOfflinePlayer(seller), cost);

                Map<String, String> placeholders = new HashMap<>();

                String price = Methods.getPrice(ID, false);

                placeholders.put("%Price%", price);
                placeholders.put("%price%", price);
                placeholders.put("%Player%", player.getName());
                placeholders.put("%player%", player.getName());

                player.sendMessage(Messages.BOUGHT_ITEM.getMessage(player, placeholders));

                if (seller != null && Methods.isOnline(seller) && Methods.getPlayer(seller) != null) {
                    Player sell = Methods.getPlayer(seller);

                    if (sell != null) {
                        sell.sendMessage(Messages.PLAYER_BOUGHT_ITEM.getMessage(player, placeholders));

                        FileConfiguration config = Files.config.getConfiguration();

                        String sound = config.getString("Settings.Sold-Item-Sound", "");

                        if (sound.isEmpty()) return;

                        try {
                            player.playSound(player.getLocation(), Sound.valueOf(sound), 1f, 1f);
                        } catch (Exception ignored) {}
                    }
                }

                player.getInventory().addItem(i);

                data.set("Items." + ID, null);
                Files.data.save();

                menu.click(player);

                GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);
            }

            case "Cancel" -> {
                GuiManager.openShop(player, HolderManager.getShopType(player), HolderManager.getShopCategory(player), 1);

                menu.click(player);
            }
        }
    }
}