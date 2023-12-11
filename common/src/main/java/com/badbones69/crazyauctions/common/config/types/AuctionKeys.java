package com.badbones69.crazyauctions.common.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class AuctionKeys implements SettingsHolder {

    protected AuctionKeys() {}

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

        conf.setComment("root", header);
    }

    @Comment("The name of the menu opened when using /ah")
    public static final Property<String> inventory_name = newProperty("root.inventory-name", "<dark_red>Crazy <blue>Auctions");
}