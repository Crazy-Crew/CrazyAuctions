package com.badbones69.crazyauctions.configs.impl.locale;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;

public class GuiKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "All messages related to guis."
        };

        conf.setComment("gui", header);
    }

    @Comment("A list of available placeholders: {price}, {seller}")
    public static final Property<List<String>> selling_lore = newListProperty("gui.button.selling-lore", List.of(
            "<gray>-------------------------",
            "<green>Click here to purchase.",
            "",
            "<blue>Price: <gold>${price}",
            "<blue>Seller: <gold>{seller}",
            "<gray>-------------------------"
    ));

    @Comment("A list of available placeholders: {price}, {time}")
    public static final Property<List<String>> current_lore = newListProperty("gui.button.current-lore", List.of(
            "<gray>-------------------------",
            "<green>Click here to cancel.",
            "",
            "<blue>Price: <gold>${price}",
            "<blue>Expire: <gold>{time}",
            "<gray>-------------------------"
    ));

    @Comment("A list of available placeholders: {time}")
    public static final Property<List<String>> cancelled_lore = newListProperty("gui.button.cancelled-lore", List.of(
            "<gray>-------------------------",
            "<green>Click here to return to you.",
            "",
            "<blue>Full Expire: <gold>{time}",
            "<gray>-------------------------"
    ));

    @Comment("A list of available placeholders: {seller}, {topbid}, {topbidder}, {time}")
    public static final Property<List<String>> bidding_lore = newListProperty("gui.button.bidding-lore", List.of(
            "<gray>-------------------------",
            "<green>Click here to bid.",
            "",
            "<blue>Seller: <gold>{seller}",
            "<blue>Current Bid: <gold>${topbid}",
            "<blue>Top Bidder: <gold>{topbidder}",
            "<blue>Time Left: <gold>{time}",
            "<gray>-------------------------"
    ));
}