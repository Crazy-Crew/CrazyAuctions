package com.badbones69.crazyauctions.configs.impl.gui;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyauctions.configs.beans.ButtonProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class AuctionKeys implements SettingsHolder {

    protected AuctionKeys() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/BlockParticles/issues",
                "Features: https://github.com/Crazy-Crew/BlockParticles/issues",
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

        conf.setComment("gui", header);
    }

    @Comment("The name of the gui.")
    public static final Property<String> gui_name = newProperty("gui.name", "<gray>[<dark_red>Crazy <blue>Auctions<gray>]: ");

    @Comment("The name of the cancelled / expired item button.")
    public static final Property<ButtonProperty> expired_item_button = newBeanProperty(ButtonProperty.class, "gui.buttons.expired-item", new ButtonProperty().populate("expired-item"));

    @Comment("The name of the selling item button.")
    public static final Property<ButtonProperty> selling_item_button = newBeanProperty(ButtonProperty.class, "gui.buttons.selling-item", new ButtonProperty().populate("selling-item"));

    @Comment("The name of the bidding item button.")
    public static final Property<ButtonProperty> bidding_item_button = newBeanProperty(ButtonProperty.class, "gui.buttons.bidding-item", new ButtonProperty().populate("bidding-item"));

    @Comment("The name of the sold item button.")
    public static final Property<ButtonProperty> sold_item_button = newBeanProperty(ButtonProperty.class, "gui.buttons.sold-item", new ButtonProperty().populate("sold-item"));

}