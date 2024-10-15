package com.badbones69.crazyauctions.configs.impl;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyauctions.configs.beans.SoundProperty;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class ConfigKeys implements SettingsHolder {

    protected ConfigKeys() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyAuctions/issues",
                "Features: https://github.com/Crazy-Crew/CrazyAuctions/issues",
                "",
                "List of all sounds: https://minecraft.wiki/w/Sounds.json#Java_Edition_values",
                "List of all enchantments: https://jd.papermc.io/paper/1.20/org/bukkit/enchantments/Enchantment.html"
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens. Please read the latest changelogs",
                ""
        };

        conf.setComment("root", header);

        conf.setComment("features", "What features should be enabled?");

        conf.setComment("sounds", "Handling the click sounds for buttons.");

        conf.setComment("patches", "Settings related to patches.");

        conf.setComment("auctions", "Settings related to how /ca functions.");

        conf.setComment("auctions.interval", "Settings related to how long items are sold for.");

        conf.setComment("auctions.price-settings", "The price settings.");
        conf.setComment("auctions.price-settings.bid", "The bid settings.");
        conf.setComment("auctions.price-settings.sell", "The sell settings.");
    }

    @Comment("The prefix in front of CrazyAuction messages")
    public static final Property<String> prefix = newProperty("root.prefix", "<gray>[<dark_red>Crazy <blue>Auctions<gray>]: ");

    @Comment("This will take into consideration, what the client's locale is set to, on join and when they change it, if this is set to true.")
    public static final Property<Boolean> per_player_locale = newProperty("root.per-player-locale.toggle", false);

    @Comment({
            "The default locale file, to display to players if the above option is set to false.",
            "",
            "A list of available localization:",
            " ⤷ en_US (English America)",
            " ⤷ de_DE (German)",
            ""
    })
    public static final Property<String> default_locale_file = newProperty("root.per-player-locale.default", "en_US");

    @Comment("Should selling be enabled?")
    public static final Property<Boolean> feature_selling_enabled = newProperty("features.selling", true);

    @Comment("Should bidding be enabled?")
    public static final Property<Boolean> feature_bidding_enabled = newProperty("features.bidding", true);

    @Comment("The sound to play when an item is sold.")
    public static final Property<SoundProperty> sold_item_sound = newBeanProperty(SoundProperty.class, "sounds.sold-item", new SoundProperty().populate());

    @Comment("The sound to play when an item is clicked.")
    public static final Property<SoundProperty> click_item_sound = newBeanProperty(SoundProperty.class, "sounds.click-item", new SoundProperty().populate());

    @Comment("Patches a duplication bug, where a player can duplicate items when logging out/being dead.")
    public static final Property<Boolean> macro_dupe_patch = newProperty("patches.macro-dupe", true);

    @Comment("Should damage items be allowed to be auctioned?")
    public static final Property<Boolean> allow_damaged_items = newProperty("auctions.allow-damaged-items", false);

    @Comment("Should the category page open first when doing ca?")
    public static final Property<Boolean> category_page_opens_first = newProperty("auctions.category-page-opens-first", false);

    @Comment("The default sell time for each item.")
    public static final Property<String> interval_sell_time = newProperty("auctions.interval.sell-time", "2d");

    @Comment("The default bid time when using /ca bid.")
    public static final Property<String> interval_bid_time = newProperty("auctions.interval.bid-time", "2m 30s");

    @Comment("The time the item is in auction, until it expires.")
    public static final Property<String> interval_expire_time = newProperty("auctions.interval.expire-time", "10d");

    @Comment("The time the winner has to claim what they won.")
    public static final Property<String> interval_winner_time = newProperty("auctions.interval.winner-time", "20d");

    @Comment("The minimum starting bid price.")
    public static final Property<Integer> price_settings_bid_minimum = newProperty("auctions.price-settings.bid.minimum", 10);

    @Comment("The maximum starting bid price.")
    public static final Property<Integer> price_settings_bid_maximum = newProperty("auctions.price-settings.bid.maximum", 1000000);

    @Comment("The minimum starting sell price.")
    public static final Property<Integer> price_settings_sell_minimum = newProperty("auctions.price-settings.sell.minimum", 10);

    @Comment("The maximum starting sell price.")
    public static final Property<Integer> price_settings_sell_maximum = newProperty("auctions.price-settings.sell.maximum", 1000000);

    @Comment("Prevent these items from being sold in /ca.")
    public static final Property<List<String>> blacklist = newListProperty("auctions.blacklist", List.of(
            "end_portal_frame",
            "bedrock"
    ));
}