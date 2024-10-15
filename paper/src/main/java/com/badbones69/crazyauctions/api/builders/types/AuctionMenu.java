package com.badbones69.crazyauctions.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.builders.gui.DynamicInventoryBuilder;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.configs.enums.Messages;
import com.badbones69.crazyauctions.configs.impl.ConfigKeys;
import com.badbones69.crazyauctions.configs.impl.gui.AuctionKeys;
import com.badbones69.crazyauctions.utils.AuctionUtils;
import com.badbones69.crazyauctions.utils.MiscUtils;
import com.badbones69.crazyauctions.utils.MsgUtils;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class AuctionMenu extends DynamicInventoryBuilder {

    private final List<String> menus = new ArrayList<>();

    public AuctionMenu(final Player player, final String title, final int rows) {
        super(player, title, rows);
    }

    private final Player player = getPlayer();
    private final PaginatedGui gui = getGui();

    @Override
    public void open() { // put the category option here maybe?
        if (this.config.getProperty(ConfigKeys.category_page_opens_first)) {
            //todo() open gui with specific category.

            return;
        }

        this.gui.setDefaultTopClickAction(event -> MiscUtils.play(player, player.getLocation(), this.config.getProperty(ConfigKeys.click_item_sound), Sound.Source.PLAYER));

        final YamlConfiguration configuration = Files.data.getConfiguration();

        AuctionUtils.getItems(configuration.getConfigurationSection("Items")).forEach(itemStack -> {
            this.gui.addItem(itemStack.asGuiItem(event -> {
                if (this.menus.contains("bidding_gui")) {
                    return;
                }

                this.menus.clear();

                this.gui.clearPageContents();
                this.gui.clearPageItems();

                if (!configuration.contains("Items")) return;

                final ConfigurationSection section = configuration.getConfigurationSection("Items");

                if (section == null) return;

                section.getKeys(false).forEach(key -> {
                    final ConfigurationSection auction = section.getConfigurationSection(key);

                    if (auction == null) return;

                    final String seller = auction.getString("Seller");

                    if (seller == null || seller.isEmpty()) return;

                    final ItemBuilder itemBuilder = new ItemBuilder().fromBase64(auction.getString("Item", ""));

                    Messages.bidding_lore.getList(this.player).forEach(itemBuilder::addDisplayLore);

                    final String price = Methods.getPrice(key, false);

                    itemBuilder.addLorePlaceholder("{price}", Methods.getPrice(key, true))
                            .addLorePlaceholder("{time}", Methods.convertToTime(auction.getLong("Full-Time")))
                            .addLorePlaceholder("{topbid}", price)
                            .addLorePlaceholder("{topbidder}", section.getString("TopBidder", "None").equalsIgnoreCase("None") ? "N/A" : MsgUtils.getPlayerName(section.getString("TopBidder", "None")))
                            .addLorePlaceholder("{seller}", MsgUtils.getPlayerName(seller));

                    this.gui.addItem(itemBuilder.asGuiItem());
                });

                this.menus.add("bidding_gui");

                this.gui.setTitle("<green>Bidding Items");
                this.gui.updateTitle(this.player);
                this.gui.updatePage();
            }));
        });

        this.auctions.getProperty(AuctionKeys.expired_item_button).setItem(event -> {
            if (this.menus.contains("expired_gui")) {
                return;
            }

            this.menus.clear();

            this.gui.clearPageContents();
            this.gui.clearPageItems();

            if (!configuration.contains("OutOfTime/Cancelled")) return;

            final ConfigurationSection section = configuration.getConfigurationSection("OutOfTime/Cancelled");

            if (section == null) return;

            section.getKeys(false).forEach(key -> {
                final ConfigurationSection auction = section.getConfigurationSection(key);

                if (auction == null) return;

                final String seller = auction.getString("Seller", "");

                if (seller.isEmpty()) return;

                final ItemBuilder itemBuilder = new ItemBuilder().fromBase64(auction.getString("Item", ""));

                Messages.cancelled_lore.getList(this.player).forEach(itemBuilder::addDisplayLore);

                itemBuilder.addLorePlaceholder("{price}", Methods.getPrice(key, true))
                        .addLorePlaceholder("{time}", Methods.convertToTime(auction.getLong("Full-Time")))
                        .addLorePlaceholder("{seller}", MsgUtils.getPlayerName(seller));

                this.gui.addItem(itemBuilder.asGuiItem());
            });

            this.menus.add("expired_gui");

            this.gui.setTitle("<green>Expired Items");
            this.gui.updateTitle(this.player);
            this.gui.updatePage();
        }, this.gui);

        // add selling item
        setSellingItem();

        this.auctions.getProperty(AuctionKeys.sold_item_button).setItem(event -> {
            if (this.menus.contains("sold_gui")) {
                return;
            }

            this.menus.clear();

            this.gui.clearPageContents();
            this.gui.clearPageItems();

            if (!configuration.contains("Items")) return;

            final ConfigurationSection section = configuration.getConfigurationSection("Items");

            if (section == null) return;

            section.getKeys(false).forEach(key -> {
                final ConfigurationSection auction = section.getConfigurationSection(key);

                if (auction == null) return;

                final String seller = auction.getString("Seller");

                if (seller == null || seller.isEmpty() || !seller.equalsIgnoreCase(player.getUniqueId().toString())) return;

                final ItemBuilder itemBuilder = new ItemBuilder().fromBase64(auction.getString("Item", ""));

                Messages.current_lore.getList(this.player).forEach(itemBuilder::addDisplayLore);

                itemBuilder.addLorePlaceholder("{price}", Methods.getPrice(key, true))
                        .addLorePlaceholder("{time}", Methods.convertToTime(auction.getLong("Full-Time")));

                this.gui.addItem(itemBuilder.asGuiItem());
            });

            this.menus.add("sold_gui");

            this.gui.setTitle("<green>Selling Items");
            this.gui.updateTitle(this.player);
            this.gui.updatePage();
        }, this.gui);

        this.gui.setOpenGuiAction(event -> {
            // Clear again, just in case.
            this.menus.clear();
        });

        this.gui.setCloseGuiAction(event -> {
            // forget the data, so no memory leak!
            this.menus.clear();
        });

        this.gui.open(this.player);
    }
}