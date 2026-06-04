package com.badbones69.crazyauctions.common.registry;

import com.badbones69.crazyauctions.common.enums.FileKey;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.core.api.registry.message.adapter.YamlMessageAdapter;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.api.constants.Messages;
import java.util.List;
import static us.crazycrew.api.constants.Messages.default_locale;

public class MessageImpl {

    private final MessageRegistry registry;

    public MessageImpl(@NonNull final MessageRegistry registry) {
        this.registry = registry;
    }

    public void reload() {
        init();
    }

    public void init() {
        this.registry.init(action -> {
            final CommentedConfigurationNode configuration = FileKey.messages.getYamlConfig();

            action.addKey(default_locale, Messages.players_only, new YamlMessageAdapter(configuration, "<red>Only players can use this command.", "Messages", "Players-Only"));

            action.addKey(default_locale, Messages.failed_to_open_inventory, new YamlMessageAdapter(configuration, "<red>Failed to open inventory.", "Messages", "Failed-To-Open-Inventory"));

            action.addKey(default_locale, Messages.reload,
                    new YamlMessageAdapter(configuration, "<gray>You have just reloaded the Crazy Auctions Files.", "Messages", "Reload"));

            action.addKey(default_locale, Messages.need_more_money,
                    new YamlMessageAdapter(configuration, "<red>You are in need of <green>$%money_needed%</green>.", "Messages", "Need-More-Money"));

            action.addKey(default_locale, Messages.inventory_full,
                    new YamlMessageAdapter(configuration, "<red>Your inventory is too full. Please open up some space to buy that.", "Messages", "Inventory-Full"));

            action.addKey(default_locale, Messages.no_permission,
                    new YamlMessageAdapter(configuration, "<red>You do not have permission to use that command!", "Messages", "No-Permission"));

            action.addKey(default_locale, Messages.not_online,
                    new YamlMessageAdapter(configuration, "<red>That player is not online.", "Messages", "Not-Online"));

            action.addKey(default_locale, Messages.doesnt_have_item_in_hand,
                    new YamlMessageAdapter(configuration, "<red>You must have an item in your hand.", "Messages", "Doesnt-Have-Item-In-Hand"));

            action.addKey(default_locale, Messages.not_a_number,
                    new YamlMessageAdapter(configuration, "<red>%arg% is not a number.", "Messages", "Not-A-Number"));

            action.addKey(default_locale, Messages.got_item_back,
                    new YamlMessageAdapter(configuration, "<gray>Your item has been returned.", "Messages", "Got-Item-Back"));

            action.addKey(default_locale, Messages.cancelled_item,
                    new YamlMessageAdapter(configuration, "<gray>You have canceled an item on the auction list, return your items with /ah expired.", "Messages", "Cancelled-Item"));

            action.addKey(default_locale, Messages.item_has_expired,
                    new YamlMessageAdapter(configuration, "<gray>An item you have in the Crazy Auctions has expired.", "Messages", "Item-Has-Expired"));

            action.addKey(default_locale, Messages.admin_force_cancelled,
                    new YamlMessageAdapter(configuration, "<gray>You have force canceled a sale.", "Messages", "Admin-Force-Cancelled"));

            action.addKey(default_locale, Messages.admin_force_cancelled_all,
                    new YamlMessageAdapter(configuration, "<gray>You have force canceled all sales.", "Messages", "Admin-Force-Cancelled-All"));

            action.addKey(default_locale, Messages.admin_force_cancelled_to_player,
                    new YamlMessageAdapter(configuration, "<red>One of your items was force canceled by an Admin.", "Messages", "Admin-Force-Cancelled-To-Player"));

            action.addKey(default_locale, Messages.item_doesnt_exist,
                    new YamlMessageAdapter(configuration, "<red>That item isn't in the crazy auctions any more.", "Messages", "Item-Doesnt-Exist"));

            action.addKey(default_locale, Messages.max_items,
                    new YamlMessageAdapter(configuration, "<red>You can't list any more items to the Crazy Auctions.", "Messages", "Max-Items"));

            action.addKey(default_locale, Messages.item_blacklisted,
                    new YamlMessageAdapter(configuration, "<red>That item is not allowed to be sold here.", "Messages", "Item-Blacklisted"));

            action.addKey(default_locale, Messages.item_damaged,
                    new YamlMessageAdapter(configuration, "<red>That item is damaged and is not allowed to be sold here.", "Messages", "Item-Damaged"));

            action.addKey(default_locale, Messages.sold_msg,
                    new YamlMessageAdapter(configuration, "<gray>Thank you for buying this item.", "Messages", "Sold-Msg"));

            action.addKey(default_locale, Messages.bid_more_money,
                    new YamlMessageAdapter(configuration, "<red>Your bid is too low, please bid more.", "Messages", "Bid-More-Money"));

            action.addKey(default_locale, Messages.not_a_currency,
                    new YamlMessageAdapter(configuration, "<red>That is not a currency. Please use Money or Tokens.", "Messages", "Not-A-Currency"));

            action.addKey(default_locale, Messages.sell_price_too_low,
                    new YamlMessageAdapter(configuration, "<red>Your sell price is too low the minimum is <green>$10</green>.", "Messages", "Sell-Price-Too-Low"));

            action.addKey(default_locale, Messages.sell_price_too_high,
                    new YamlMessageAdapter(configuration, "<red>Your sell price is too high the maximum is <green>$1000000</green>.", "Messages", "Sell-Price-Too-High"));

            action.addKey(default_locale, Messages.bid_price_too_low,
                    new YamlMessageAdapter(configuration, "<red>Your starting bid price is too low the minimum is <green>$100</green>.", "Messages", "Bid-Price-Too-Low"));

            action.addKey(default_locale, Messages.bid_price_too_high,
                    new YamlMessageAdapter(configuration, "<red>Your starting bid price is too high the maximum is <green>$1000000</green>.", "Messages", "Bid-Price-Too-High"));

            action.addKey(default_locale, Messages.bought_item,
                    new YamlMessageAdapter(configuration, "<gray>You have just bought an item for <green>$%price%</green>.", "Messages", "Bought-Item"));

            action.addKey(default_locale, Messages.win_bidding,
                    new YamlMessageAdapter(configuration, "<gray>You have just won a bid for <green>$%price%</green>. Do /Ah Collect to collect your winnings.", "Messages", "Win-Bidding"));

            action.addKey(default_locale, Messages.player_bought_item,
                    new YamlMessageAdapter(configuration, "<gray>%player% has bought your item for <green>$%price%</green>.", "Messages", "Player-Bought-Item"));

            action.addKey(default_locale, Messages.someone_won_players_bid,
                    new YamlMessageAdapter(configuration, "<gray>%player% has won your item with a bid of <green>$%price%</green>.", "Messages", "Someone-Won-Players-Bid"));

            action.addKey(default_locale, Messages.added_item_to_auction,
                    new YamlMessageAdapter(configuration, "<gray>You have just added an item to the crazy auctions for <green>$%price%</green>.", "Messages", "Added-Item-To-Auction"));

            action.addKey(default_locale, Messages.bid_msg,
                    new YamlMessageAdapter(configuration, "<gray>You have just bid <green>$%Bid%</green> on that item.", "Messages", "Bid-Msg"));

            action.addKey(default_locale, Messages.selling_disabled,
                    new YamlMessageAdapter(configuration, "<red>The selling option is disabled.", "Messages", "Selling-Disabled"));

            action.addKey(default_locale, Messages.bidding_disabled,
                    new YamlMessageAdapter(configuration, "<red>The bidding option is disabled.", "Messages", "Bidding-Disabled"));

            action.addKey(default_locale, Messages.book_not_allowed,
                    new YamlMessageAdapter(configuration, "<red>That book is not able to be sold in this auction house!", "Messages", "Book-Not-Allowed"));

            action.addKey(default_locale, Messages.help_menu, new YamlMessageAdapter(configuration, StringUtils.toString(List.of(
                    "<yellow>-- <gold>Crazy Auctions Help</gold> --",
                    "<blue>/Ah</blue> <gray>- Opens the crazy auction.",
                    "<blue>/Ah View <Player></blue> <gray>- See what a player is selling.",
                    "<blue>/Ah Sell/Bid <Price> [Amount]</blue> <gray>- List the item you are holding.",
                    "<blue>/Ah Expired/Collect</blue> <gray>- Manage canceled and expired items.",
                    "<blue>/Ah Listed</blue> <gray>- View items you are selling.",
                    "<blue>/Ah Help</blue> <gray>- View this help menu."
            )), "Messages", "Help-Menu"));
        });
    }
}