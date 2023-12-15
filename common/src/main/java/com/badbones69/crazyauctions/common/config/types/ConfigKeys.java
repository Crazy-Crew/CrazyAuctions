package com.badbones69.crazyauctions.common.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyauctions.common.enums.StorageType;

import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;
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
                "List of all sounds: https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html",
                "List of all enchantments: https://jd.papermc.io/paper/1.20/org/bukkit/enchantments/Enchantment.html"
        };

        String[] modules = {
                "A section related to modules that can be disabled or enabled.",
                "",
                "Each module will be unregistered if the toggle is set to false",
                "You simply need to run /crazyauctions reload"
        };

        conf.setComment("root", header);

        conf.setComment("modules", modules);

        String[] patches = {
                "A section related to patches which you can disable for whatever reason.",
                "This section is only here for if the patch causes more issues then what it's meant to fix.",
                "",
                "Patches will allow players to dupe or crash your server."
        };

        conf.setComment("patches", patches);

        conf.setComment("misc", "Settings that don't really belong anywhere.");

        conf.setComment("auctions", "Settings related to default auction values.");
    }

    @Comment("Whether you want CrazyAuctions to shut up or not, This option is ignored by errors.")
    public static final Property<Boolean> verbose_logging = newProperty("root.verbose_logging", true);

    @Comment({
            "Sends anonymous statistics about how the plugin is used to bstats.org.",
            "bstats is a service for plugin developers to find out how the plugin being used,",
            "This information helps us figure out how to better improve the plugin."
    })
    public static final Property<Boolean> toggle_metrics = newProperty("root.toggle_metrics", true);

    @Comment("Pick which locale you want to use if your server is in another language. Changing this requires a server restart!")
    public static final Property<String> locale_file = newProperty("root.locale", "en-US");

    @Comment({
            "How the plugin should store data",
            "",
            " - Your Options",
            " | Remote Database Types - You need to supply connection information.",
            "  |» MySQL *NOT IMPLEMENTED*",
            "  |» MariaDB *NOT IMPLEMENTED*",
            "",
            " | Local Database Types",
            "  |» H2 *NOT IMPLEMENTED*",
            "  |» SQLITE *DEFAULT",
            "",
            " | Text File Based Storage",
            "  |» JSON (.json files) *NOT IMPLEMENTED*"
    })
    public static final Property<StorageType> storage_type = newBeanProperty(StorageType.class, "root.database.storage-method", StorageType.SQLITE);

    @Comment("The prefix used in commands")
    public static final Property<String> command_prefix = newProperty("root.command_prefix", " <dark_gray>[<dark_red>Crazy<blue>Auctions<dark_gray>]: <reset>");

    @Comment("The prefix used in console")
    public static final Property<String> console_prefix = newProperty("root.console_prefix", " <dark_gray>[<dark_red>Crazy<blue>Auctions<dark_gray>] <reset>");

    @Comment("If true, /ah sell <price> is enabled.")
    public static final Property<Boolean> selling_module = newProperty("modules.selling", true);

    @Comment("If true, /ah bid is enabled.")
    public static final Property<Boolean> bidding_module = newProperty("modules.bidding", true);

    @Comment("If true, a macro dupe should be fixed.")
    public static final Property<Boolean> macro_patch = newProperty("patches.macro-dupe", true);

    @Comment("If true, a sound will be played.")
    public static final Property<Boolean> sound_toggle = newProperty("misc.sounds.toggle", true);

    @Comment({
            "A list of sounds you can use: https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html",
    })
    public static final Property<String> sound_value = newProperty("misc.sounds.value", "UI_BUTTON_CLICK");

    @Comment("The min price needed to sell an item.")
    public static final Property<Integer> min_sell_price = newProperty("auctions.sell.min-price", 10);

    @Comment("The max price allowed for selling items.")
    public static final Property<Integer> max_sell_price = newProperty("auctions.sell.max-price", 100000);

    @Comment("The min price a starting bid has to be.")
    public static final Property<Integer> min_bid_price = newProperty("auctions.bid.min-price", 10);

    @Comment("The max price a starting bid can be.")
    public static final Property<Integer> max_bid_price = newProperty("auctions.bid.max-price", 100000);

    @Comment("Allow damaged items to be sold in the auction house.")
    public static final Property<Boolean> allow_damaged_items = newProperty("auctions.items.allow-damaged-items", false);

    @Comment("How long each item will be sold for before expiring.")
    public static final Property<String> default_sell_time = newProperty("auctions.default-sell-time", "3d");

    @Comment("The default time an item will be on the auction house.")
    public static final Property<String> default_bid_time = newProperty("auctions.default-bid-time", "2m 30s");

    @Comment("How long should it take before an item is expired?")
    public static final Property<String> default_expire_time = newProperty("auctions.default-expire-item", "15d");

    @Comment("How long should an item be claimable by the winner of the bid.")
    public static final Property<String> default_collect_time = newProperty("auctions.default-collect-time", "30d");

    @Comment({
            "The economy you wish to use for the plugin.",
            "",
            "Available Types: vault, xp_total, xp_levels"
    })
    public static final Property<String> economy_type = newProperty("auctions.economy", "vault");
}