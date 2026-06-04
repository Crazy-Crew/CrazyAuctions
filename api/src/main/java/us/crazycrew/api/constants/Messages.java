package us.crazycrew.api.constants;

import com.ryderbelserion.fusion.core.api.FusionKey;
import net.kyori.adventure.key.Key;
import static us.crazycrew.api.CrazyAuctions.namespace;

public class Messages {

    public static final FusionKey default_locale = FusionKey.key(namespace, "default");

    public static final FusionKey players_only = FusionKey.key(namespace, "players_only");

    public static final FusionKey failed_to_open_inventory = FusionKey.key(namespace, "failed_to_open_inventory");

    public static final FusionKey reload = FusionKey.key(namespace, "reload");

    public static final FusionKey need_more_money = FusionKey.key(namespace, "need_more_money");
    public static final FusionKey inventory_full = FusionKey.key(namespace, "inventory_full");
    public static final FusionKey no_permission = FusionKey.key(namespace, "no_permission");
    public static final FusionKey not_online = FusionKey.key(namespace, "not_online");

    public static final FusionKey doesnt_have_item_in_hand = FusionKey.key(namespace, "doesnt_have_item_in_hand");
    public static final FusionKey not_a_number = FusionKey.key(namespace, "not_a_number");
    public static final FusionKey got_item_back = FusionKey.key(namespace, "got_item_back");
    public static final FusionKey cancelled_item = FusionKey.key(namespace, "cancelled_item");
    public static final FusionKey item_has_expired = FusionKey.key(namespace, "item_has_expired");
    public static final FusionKey admin_force_cancelled = FusionKey.key(namespace, "admin_force_cancelled");
    public static final FusionKey admin_force_cancelled_all = FusionKey.key(namespace, "admin_force_cancelled_all");
    public static final FusionKey admin_force_cancelled_to_player = FusionKey.key(namespace, "admin_force_cancelled_to_player");
    public static final FusionKey item_doesnt_exist = FusionKey.key(namespace, "item_doesnt_exist");
    public static final FusionKey max_items = FusionKey.key(namespace, "max_items");
    public static final FusionKey item_blacklisted = FusionKey.key(namespace, "item_blacklisted");
    public static final FusionKey item_damaged = FusionKey.key(namespace, "item_damaged");
    public static final FusionKey sold_msg = FusionKey.key(namespace, "sold_msg");
    public static final FusionKey bid_more_money = FusionKey.key(namespace, "bid_more_money");
    public static final FusionKey not_a_currency = FusionKey.key(namespace, "not_a_currency");

    public static final FusionKey sell_price_too_low = FusionKey.key(namespace, "sell_price_too_low");
    public static final FusionKey sell_price_too_high = FusionKey.key(namespace, "sell_price_too_high");
    public static final FusionKey bid_price_too_low = FusionKey.key(namespace, "bid_price_too_low");
    public static final FusionKey bid_price_too_high = FusionKey.key(namespace, "bid_price_too_high");

    public static final FusionKey bought_item = FusionKey.key(namespace, "bought_item");
    public static final FusionKey win_bidding = FusionKey.key(namespace, "win_bidding");

    public static final FusionKey player_bought_item = FusionKey.key(namespace, "player_bought_item");

    public static final FusionKey someone_won_players_bid = FusionKey.key(namespace, "someone_won_players_bid");
    public static final FusionKey added_item_to_auction = FusionKey.key(namespace, "added_item_to_auction");
    public static final FusionKey bid_msg = FusionKey.key(namespace, "bid_msg");

    public static final FusionKey selling_disabled = FusionKey.key(namespace, "selling_disabled");
    public static final FusionKey bidding_disabled = FusionKey.key(namespace, "bidding_disabled");

    public static final FusionKey book_not_allowed = FusionKey.key(namespace, "book_not_allowed");

    public static final FusionKey help_menu = FusionKey.key(namespace, "help_menu");

    public static final FusionKey migration_error = FusionKey.key(namespace, "migration_error");

    public static final FusionKey migration_not_available = FusionKey.key(namespace, "migration_not_available");

    public static final FusionKey migration_success = FusionKey.key(namespace, "migration_success");
}