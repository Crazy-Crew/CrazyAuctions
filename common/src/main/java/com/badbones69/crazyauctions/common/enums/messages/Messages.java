package com.badbones69.crazyauctions.common.enums.messages;

import us.crazycrew.api.adapters.sender.ISenderAdapter;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.config.properties.objects.enums.PropertyType;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.core.api.registry.message.adapter.YamlMessageAdapter;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.AuctionsProvider;
import us.crazycrew.api.CrazyAuctions;
import java.util.List;
import java.util.Map;
import static us.crazycrew.api.CrazyAuctions.namespace;

@ApiStatus.Internal
@NullMarked
public enum Messages {

    players_only("messages.players-only", "{prefix}<red>Only players can use this command.", "Messages", "Players-Only"),
    failed_to_open_inventory("messages.failed-to-open-inventory", "{prefix}<red>Failed to open inventory.", "Messages", "Failed-To-Open-Inventory"),
    reload("messages.reload", "{prefix}<gray>You have just reloaded the Crazy Auctions Files.", "Messages", "Reload"),
    need_more_money("messages.need-more-money", "{prefix}<red>You are in need of <green>$%money_needed%</green>.", "Messages", "Need-More-Money"),
    transaction_failed("messages.transaction-failed", "{prefix}<red>The transaction could not be completed. No item or money was transferred.", "Messages", "Transaction-Failed"),
    inventory_full("messages.inventory-full", "{prefix}<red>Your inventory is too full. Please open up some space to buy that.", "Messages", "Inventory-Full"),
    no_permission("messages.no-permission", "{prefix}<red>You do not have permission to use that command!", "Messages", "No-Permission"),
    not_online("messages.not-online", "{prefix}<red>That player is not online.", "Messages", "Not-Online"),
    doesnt_have_item_in_hand("messages.doesnt-have-item-in-hand", "{prefix}<red>You must have an item in your hand.", "Messages", "Doesnt-Have-Item-In-Hand"),
    not_a_number("messages.not-a-number", "{prefix}<red>%arg% is not a number.", "Messages", "Not-A-Number"),
    got_item_back("messages.got-item-back", "{prefix}<gray>Your item has been returned.", "Messages", "Got-Item-Back"),
    cancelled_item("messages.cancelled-item", "{prefix}<gray>You have canceled an item on the auction list, return your items with /ah expired.", "Messages", "Cancelled-Item"),
    item_has_expired("messages.item-has-expired", "{prefix}<gray>An item you have in the Crazy Auctions has expired.", "Messages", "Item-Has-Expired"),
    admin_force_cancelled("messages.admin-force-cancelled", "{prefix}<gray>You have force canceled a sale.", "Messages", "Admin-Force-Cancelled"),
    admin_force_cancelled_all("messages.admin-force-cancelled-all", "{prefix}<gray>You have force canceled all sales.", "Messages", "Admin-Force-Cancelled-All"),
    admin_force_cancelled_to_player("mesages.admin-force-cancelled-to-player", "{prefix}<red>One of your items was force canceled by an Admin.", "Messages", "Admin-Force-Cancelled-To-Player"),
    item_doesnt_exist("messages.item-doesnt-exist", "{prefix}<red>That item isn't in the crazy auctions any more.", "Messages", "Item-Doesnt-Exist"),
    max_items("messages.max-items", "{prefix}<red>You can't list any more items to the Crazy Auctions.", "Messages", "Max-Items"),
    item_blacklisted("messages.item-blacklisted", "{prefix}<red>That item is not allowed to be sold here.", "Messages", "Item-Blacklisted"),
    item_damaged("messages.item-damaged", "{prefix}<red>That item is damaged and is not allowed to be sold here.", "Messages", "Item-Damaged"),
    sold_msg("messages.sold-msg", "{prefix}<gray>Thank you for buying this item.", "Messages", "Sold-Msg"),
    bid_more_money("messages.bid-more-money", "{prefix}<red>Your bid is too low, please bid more.", "Messages", "Bid-More-Money"),
    not_a_currency("messages.not-a-currency", "{prefix}<red>That is not a currency. Please use Money or Tokens.", "Messages", "Not-A-Currency"),
    sell_price_too_low("messages.sell-price-too-low", "{prefix}<red>Your sell price is too low the minimum is <green>$10</green>.", "Messages", "Sell-Price-Too-Low"),
    sell_price_too_high("messages.sell-price-too-high", "{prefix}<red>Your sell price is too high the maximum is <green>$1000000</green>.", "Messages", "Sell-Price-Too-High"),
    bid_price_too_low("messages.bid-price-too-low", "{prefix}<red>Your starting bid price is too low the minimum is <green>$100</green>.", "Messages", "Bid-Price-Too-Low"),
    bid_price_too_high("messages.bid-price-too-high", "{prefix}<red>Your starting bid price is too high the maximum is <green>$1000000</green>.", "Messages", "Bid-Price-Too-High"),
    bought_item("messages.bought-item", "{prefix}<gray>You have just bought an item for <green>$%price%</green>.", "Messages", "Bought-Item"),
    win_bidding("messages.win-bidding", "{prefix}<gray>You have just won a bid for <green>$%price%</green>. Do /Ah Collect to collect your winnings.", "Messages", "Win-Bidding"),
    player_bought_item("messages.player-bought-item", "{prefix}<gray>%player% has bought your item for <green>$%price%</green>.", "Messages", "Player-Bought-Item"),
    someone_won_players_bid("messages.someone-won-players-bid", "{prefix}<gray>%player% has won your item with a bid of <green>$%price%</green>.", "Messages", "Someone-Won-Players-Bid"),
    buyer_tax("messages.buyer-tax", "{prefix}<gray>Tax paid: <green>$%buyer_tax%</green>. Total charged: <green>$%buyer_total%</green>.", "Messages", "Buyer-Tax"),
    seller_tax("messages.seller-tax", "{prefix}<gray>Tax paid: <green>$%seller_tax%</green>. Proceeds received: <green>$%seller_proceeds%</green>.", "Messages", "Seller-Tax"),
    added_item_to_auction("messages.added-item-to-auction", "{prefix}<gray>You have just added an item to the crazy auctions for <green>$%price%</green>.", "Messages", "Added-Item-To-Auction"),
    bid_msg("messages.bid-msg", "{prefix}<gray>You have just bid <green>$%Bid%</green> on that item.", "Messages", "Bid-Msg"),
    selling_disabled("messages.selling-disabled", "{prefix}<red>The selling option is disabled.", "Messages", "Selling-Disabled"),
    bidding_disabled("messages.bidding-disabled", "{prefix}<red>The bidding option is disabled.", "Messages", "Bidding-Disabled"),
    book_not_allowed("messages.book-not-allowed", "{prefix}<red>That book is not able to be sold in this auction house!", "Messages", "Book-Not-Allowed"),
    migration_error("messages.migration.error", "{prefix}<red>We could not migrate <green>{file} <red>using <green>{type} <red>migration for <green>{reason}.", "Messages", "Migration", "Error"),
    migration_not_available("messages.migration.not-available", "{prefix}<green>This migration type is not available.", "Messages", "Migration", "Not-Available"),
    migration_success("messages.migration.success", List.of(
            "<bold><gold>━━━━━━━━━━━━━━━━━━━ Migration Stats ━━━━━━━━━━━━━━━━━━━</gold></bold>",
            "<dark_gray>»</dark_gray> <green>Successful Conversions: ",
            " ⤷ {succeeded_amount}</green>",
            "<dark_gray>»</dark_gray> <red>Failed Conversions: ",
            " ⤷ {failed_amount}</red>",
            "",
            "<red>Conversion Time: <yellow>{time}",
            "<red>Conversion Type: <yellow>{type}",
            "",
            "<red>Converted Files:",
            "{files}",
            "",
            "<bold><gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gold></bold>"
    ), "Messages", "Migration", "Success"),
    help_menu("messages.help", List.of(
            "<yellow>-- <gold>Crazy Auctions Help</gold> --",
            "<blue>/Ah</blue> <gray>- Opens the crazy auction.",
            "<blue>/Ah View <Player></blue> <gray>- See what a player is selling.",
            "<blue>/Ah Sell/Bid <Price> [Amount]</blue> <gray>- List the item you are holding.",
            "<blue>/Ah Expired/Collect</blue> <gray>- Manage canceled and expired items.",
            "<blue>/Ah Listed</blue> <gray>- View items you are selling.",
            "<blue>/Ah Help</blue> <gray>- View this help menu."
    ), "Messages", "Help-Menu");

    private final CrazyAuctions platform = AuctionsProvider.api();

    private final ISenderAdapter senderAdapter = this.platform.getSenderAdapter();

    //private final ConfigManager configManager = this.platform.getConfigManager();

    //private final PropertyManager pluginConfig = this.configManager.getConfig();

    private final String defaultValue;
    private final PropertyType type;
    private final Object[] path;
    private final FusionKey id;

    Messages(final String id, final String defaultValue, final Object... path) {
        this.defaultValue = defaultValue;
        this.id = FusionKey.key(namespace, id);
        this.type = PropertyType.STRING;
        this.path = path;
    }

    Messages(final String id, final List<String> defaultValue, final Object... path) {
        this.defaultValue = StringUtils.toString(defaultValue);
        this.id = FusionKey.key(namespace, id);
        this.type = PropertyType.STRING_LIST;
        this.path = path;
    }

    public void addKey(final MessageRegistry registry, final CommentedConfigurationNode configuration, final FusionKey id) {
        final YamlMessageAdapter adapter = new YamlMessageAdapter(configuration, this.defaultValue, this.path);

        /*if (!configuration.hasChild(this.path)) {
            final CommentedConfigurationNode section = configuration.node(this.path);

            switch (this.type) {
                case STRING_LIST -> {
                    try {
                        section.set(toList(this.defaultValue));

                        customFile.save();
                    } catch (final SerializationException exception) {
                        exception.printStackTrace();
                    }
                }

                case STRING -> {
                    try {
                        section.set(this.defaultValue);

                        customFile.save();
                    } catch (final SerializationException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }*/

        registry.addKey(
                id,
                this.id,
                adapter
        );
    }

    public List<String> toList(final String value) {
        return value.lines().toList();
    }

    public void sendMessage(final Audience audience, final Map<String, String> placeholders) {
        this.senderAdapter.sendMessage(audience, this.id, placeholders);

        //switch (this.pluginConfig.getProperty(RootKeys.get_message_state)) {
        //    case send_message -> this.senderAdapter.sendMessage(audience, this.id, placeholders);
        //    case send_actionbar -> this.senderAdapter.sendActionBar(audience, this.id, placeholders);
        //}
    }

    public void sendMessage(final Audience audience, final String placeholder, final String value) {
        sendMessage(audience, Map.of(placeholder, value));
    }

    public void sendMessage(final Audience audience) {
        sendMessage(audience, Map.of());
    }

    public String getMessage(final Audience audience, final Map<String, String> placeholders) {
        return this.senderAdapter.getMessage(audience, this.id, placeholders);
    }

    public String getMessage(final Audience audience, final String placeholder, final String value) {
        return getMessage(audience, Map.of(placeholder, value));
    }

    public String getMessage(final Audience audience) {
        return this.senderAdapter.getMessage(audience, this.id);
    }

    public FusionKey getKey() {
        return this.id;
    }
}
