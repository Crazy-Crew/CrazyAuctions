package com.badbones69.crazyauctions.api.builders.gui.types;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.CrazyPlatform;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.common.adapters.ServerAdapter;
import com.badbones69.crazyauctions.common.enums.FileKey;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import com.ryderbelserion.fusion.paper.builders.folia.Scheduler;
import com.ryderbelserion.fusion.paper.builders.gui.enums.GuiState;
import com.ryderbelserion.fusion.paper.builders.gui.objects.GuiItem;
import com.ryderbelserion.fusion.paper.builders.gui.types.paginated.PaginatedGui;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.api.enums.ShopType;
import us.crazycrew.api.enums.server.ServerState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionMenu {

    private final CrazyAuctions plugin = CrazyAuctions.get();
    private final VaultSupport support = this.plugin.getSupport();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final ServerAdapter serverAdapter = this.platform.getServerAdapter();

    private final PaginatedGui gui;
    private final Player player;

    public AuctionMenu(@NonNull final Player player, @NonNull final CommentedConfigurationNode configuration) {
        final CommentedConfigurationNode section = configuration.node("settings", "gui");

        this.gui = PaginatedGui.gui(
                this.plugin,
                this.player = player,
                section.node("name").getString("<red>Crazy<dark_blue>Auctions"),
                section.node("rows").getInt(6)
        );

        this.gui.addState(GuiState.block_all_interactions);
    }

    public Map<Integer, ItemStack> getButtons(@NonNull final ShopType shopType, @NonNull final ConfigurationSection settings) {
        final Map<Integer, ItemStack> items = new HashMap<>();

        final List<String> options = new ArrayList<>();

        options.add("SellingItems");
        options.add("Cancelled/ExpiredItems");
        options.add("PreviousPage");

        options.add("Refresh");
        options.add("Refesh");

        options.add("NextPage");

        options.add("Category1");
        options.add("Category2");

        switch (shopType) {
            case BID -> {
                if (this.platform.isSellModuleEnabled()) {
                    options.add("Bidding/Selling.Bidding");
                }

                options.add("WhatIsThis.BiddingShop");
            }

            case SELL -> {
                if (this.platform.isBidModuleEnabled()) {
                    options.add("Bidding/Selling.Selling");
                }

                options.add("WhatIsThis.SellingShop");
            }
        }

        final ConfigurationSection other = settings.getConfigurationSection("OtherSettings");

        if (other == null) { //todo() logging
            return new HashMap<>();
        }

        for (final String option : options) {
            if (option.isBlank()) continue;

            final ConfigurationSection section = other.getConfigurationSection(option);

            if (section == null) continue; //todo() logging

            if (!section.getBoolean("Toggle", false)) continue; //todo() logging

            final String item = section.getString("Item", "");

            if (item.isBlank()) continue; //todo() logging

            final int slot = section.getInt("Slot", 0);

            if (slot <= 0) continue; //todo() logging

            final Map<String, String> placeholders = Map.of(
                    "%Category%", "N/A",
                    "%category%", "N/A"
            );

            final ItemBuilder builder = new ItemBuilder()
                    .setMaterial(item)
                    .setName(section.getString("Name", ""))
                    .setLore(section.getStringList("Lore"))
                    .setAmount(section.getInt("Amount", 1))
                    .setLorePlaceholders(placeholders)
                    .setNamePlaceholders(placeholders);

            final ItemStack itemStack = builder.build();

            itemStack.editPersistentDataContainer(container -> {
                final Keys button_id = Keys.auction_button;

                container.set(button_id.getNamespacedKey(), button_id.getType(), option);
            });

            items.put(slot, itemStack);
        }

        return items;
    }

    public void build(@NonNull final ShopType shopType) {
        final UUID uuid = this.player.getUniqueId();
        final String asString = uuid.toString();

        if (this.serverAdapter.hasState(ServerState.auctions_frozen)) {
            this.player.sendRichMessage("<red>You cannot interact with auctions while an admin is cancelling auctions.");

            return;
        }

        final YamlConfiguration config = FileKey.config.getConfiguration();

        final ConfigurationSection settings = config.getConfigurationSection("Settings.GUISettings");

        if (settings == null) {
            return;
        }

        for (final Map.Entry<Integer, ItemStack> entries : getButtons(shopType, settings).entrySet()) {
            final int slot = entries.getKey();
            final ItemStack itemStack = entries.getValue();

            this.gui.addSlotAction(slot-1, itemStack, event -> {
                final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

                final Keys auction_button = Keys.auction_button;

                final String option = container.getOrDefault(auction_button.getNamespacedKey(), auction_button.getType(), "");

                if (option.isBlank()) return; //todo() debug

                this.player.sendRichMessage("<red>Option %s".formatted(option));
            });
        }

        final List<ItemStack> items = new ArrayList<>();

        final YamlConfiguration data = FileKey.data.getConfiguration();

        final ConfigurationSection section = data.getConfigurationSection("Items");

        if (section != null) {
            for (final String id : section.getKeys(false)) {
                if (id.isBlank()) continue;

                final ConfigurationSection item = section.getConfigurationSection(id);

                if (item == null) continue;

                final String value = item.getString("Item", "");

                if (value.isBlank()) continue;

                final ItemBuilder builder = ItemBuilder.convertItemStack(value);

                final Map<String, String> placeholders = new HashMap<>();

                final String seller = item.getString("SellerName", "N/A");
                final String price = StringUtils.formatNumber(item.getDouble("Price", 0.0));
                final String time = Methods.convertToTime(item.getLong("Time-Till-Expire", 0L));

                placeholders.putIfAbsent("%Seller%", seller);
                placeholders.putIfAbsent("%seller%", seller);

                placeholders.putIfAbsent("%Price%", price);
                placeholders.putIfAbsent("%price%", price);

                placeholders.putIfAbsent("%Time%", time);
                placeholders.putIfAbsent("%time%", time);

                final boolean isBiddable = item.getBoolean("Biddable", false);

                final List<String> lore = shopType == ShopType.BID ? settings.getStringList("Bidding") : settings.getStringList("SellingItemLore");

                builder.setAmount(item.getInt("Amount", 1)).setLore(lore).setLorePlaceholders(placeholders).setNamePlaceholders(placeholders);

                final ItemStack itemStack = builder.build();

                final String store_id = item.getString("StoreID", "");

                itemStack.editPersistentDataContainer(container -> {
                    final Keys auctionKey = Keys.auction_store_id;

                    container.set(auctionKey.getNamespacedKey(), auctionKey.getType(), store_id);

                    final Keys sellerKey = Keys.auction_seller_name;

                    container.set(sellerKey.getNamespacedKey(), sellerKey.getType(), item.getString("Seller", ""));

                    final Keys auctionId = Keys.auction_id;

                    container.set(auctionId.getNamespacedKey(), auctionId.getType(), id);

                    if (isBiddable) {
                        final Keys auctionStatus = Keys.auction_biddable;

                        container.set(auctionStatus.getNamespacedKey(), auctionStatus.getType(), true);
                    }
                });

                items.add(itemStack);
            }
        }

        for (final ItemStack itemStack : items) {
            if (itemStack.isEmpty()) continue;

            this.gui.addPageItem(new GuiItem(itemStack, event -> {
                final ItemStack eventItem = event.getCurrentItem();

                if (eventItem == null || eventItem.isEmpty()) return;

                final PersistentDataContainerView container = eventItem.getPersistentDataContainer();

                final YamlConfiguration storage = FileKey.data.getConfiguration();

                final ConfigurationSection item = storage.getConfigurationSection("Items");

                if (item == null) return;

                final ConfigurationSection auction = item.getConfigurationSection(container.getOrDefault(Keys.auction_id.getNamespacedKey(), PersistentDataType.STRING, ""));

                if (auction == null) return;

                final Inventory inventory = this.gui.getInventory();
                final int slot = event.getRawSlot();

                final String seller = container.getOrDefault(Keys.auction_seller_name.getNamespacedKey(), PersistentDataType.STRING, "");

                final Keys auctionId = Keys.auction_id;
                final String id = container.getOrDefault(auctionId.getNamespacedKey(), auctionId.getType(), "");

                /**if (Permissions.admin_wildcard.hasPermission(this.player) || Permissions.force_end.hasPermission(this.player)) { //todo() convert this to a command an admin has to run.
                    Optional.ofNullable(this.server.getPlayer(seller)).ifPresent(target -> this.adapter.sendMessage(target, Messages.admin_force_cancelled_to_player));

                    //todo() this is retarded, I should replace this with a more lightweight player object specifically for auctions instead of forcing i/o operations for the entire player object.
                    Methods.expireItem(1, Methods.getOfflinePlayer(seller), id, data, Reasons.ADMIN_FORCE_CANCEL);

                    FileKey.data.save();

                    this.adapter.sendMessage(this.player, Messages.admin_force_cancelled);

                    for (final Player target : this.server.getOnlinePlayers()) {
                        final UUID source = target.getUniqueId();

                        if (source.toString().equals(asString)) continue;

                        this.userRegistry.getUser(source).ifPresent(user -> {
                            if (user.isFrozen()) return;

                            user.setFrozen();

                            new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
                                @Override
                                public void run() {
                                    user.setFrozen();
                                }
                            }.runDelayed(80);
                        });
                    }

                    return;
                }**/

                if (this.serverAdapter.hasState(ServerState.auctions_frozen)) {
                    this.player.sendRichMessage("<red>You cannot interact with auctions while an admin is cancelling auctions.");

                    this.gui.close(this.player, InventoryCloseEvent.Reason.CANT_USE, true);

                    return;
                }

                if (!seller.isBlank() && seller.equals(asString)) {
                    String itemName = settings.getString("OtherSettings.Your-Item.Item");
                    String name = settings.getString("OtherSettings.Your-Item.Name");

                    ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                    if (settings.contains("OtherSettings.Your-Item.Lore")) {
                        itemBuilder.setLore(settings.getStringList("OtherSettings.Your-Item.Lore"));
                    }

                    inventory.setItem(slot, itemBuilder.build());

                    new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
                        @Override
                        public void run() {
                            inventory.setItem(slot, eventItem);
                        }
                    }.runDelayed(60);

                    return;
                }

                final double price = auction.getDouble("Price", 0.0);

                if (this.support.getMoney(this.player) < price) {
                    String itemName = settings.getString("OtherSettings.Cant-Afford.Item");
                    String name = settings.getString("OtherSettings.Cant-Afford.Name");

                    ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                    if (settings.contains("OtherSettings.Cant-Afford.Lore")) {
                        itemBuilder.setLore(settings.getStringList("OtherSettings.Cant-Afford.Lore"));
                    }

                    inventory.setItem(slot, itemBuilder.build());

                    new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
                        @Override
                        public void run() {
                            inventory.setItem(slot, eventItem);
                        }
                    }.runDelayed(60);

                    return;
                }

                final boolean isBiddable = container.has(Keys.auction_biddable.getNamespacedKey());

                final String currentBidder = auction.getString("TopBidder", "");

                if (isBiddable) {
                    if (!currentBidder.isBlank() && currentBidder.equals(asString)) {
                        String itemName = settings.getString("OtherSettings.Top-Bidder.Item");
                        String name = settings.getString("OtherSettings.Top-Bidder.Name");

                        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemName).setName(name).setAmount(1);

                        if (settings.contains("OtherSettings.Top-Bidder.Lore")) {
                            itemBuilder.setLore(settings.getStringList("OtherSettings.Top-Bidder.Lore"));
                        }

                        inventory.setItem(slot, itemBuilder.build());

                        new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
                            @Override
                            public void run() {
                                inventory.setItem(slot, eventItem);
                            }
                        }.runDelayed(60);

                        return;
                    }

                    GuiListener.openBidding(this.player, id);

                    return;
                }

                GuiListener.openBuying(this.player, id);
            }));
        }

        final int page = this.gui.getPageNumber();

        this.gui.setTitle(this.player, this.gui.getTitle(), Map.of(
                "{page}", String.valueOf(page),
                "%page%", String.valueOf(page)
        )); // update title to include page number, and update this every time we hit next. currently my packet to update the inventory title no longer fucking works.

        this.gui.setDefaultAction(event -> {
            final ItemStack itemStack = event.getCurrentItem();

            if (itemStack == null) return;

            if (itemStack.isEmpty()) return;

            this.player.playSound(this.player, Sound.UI_BUTTON_CLICK, 1f, 1f);
        });

        this.gui.open(this.player, 1);
    }
}