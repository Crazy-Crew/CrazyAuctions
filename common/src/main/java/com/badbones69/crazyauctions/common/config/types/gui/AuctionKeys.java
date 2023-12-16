package com.badbones69.crazyauctions.common.config.types.gui;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class AuctionKeys implements SettingsHolder {

    protected AuctionKeys() {}

    @Comment("The name of the menu opened when using /ah")
    public static final Property<String> inventory_name = newProperty("auctions.base.inventory-name", "<dark_red>Crazy<blue>Auctions");

}