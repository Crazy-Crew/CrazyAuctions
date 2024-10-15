package com.badbones69.crazyauctions.configs.impl.locale;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class PlayerKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "All messages related to players."
        };

        conf.setComment("player", header);
    }

    public static final Property<String> must_be_a_player = newProperty("player.requirements.must-be-player", "{prefix}<red>You must be a player to use this command.");

    public static final Property<String> must_be_console_sender = newProperty("player.requirements.must-be-console-sender", "{prefix}<red>You must be using console to use this command.");

    @Comment("A list of available placeholders: {player}")
    public static final Property<String> not_online = newProperty("player.target-not-online", "{prefix}<red>{player} <gray>is not online.");

    public static final Property<String> same_player = newProperty("player.target-same-player", "{prefix}<red>You cannot use this command on yourself.");

    public static final Property<String> no_permission = newProperty("player.no-permission", "{prefix}<red>You do not have permission to use that command!");

    public static final Property<String> inventory_not_empty = newProperty("player.inventory-not-empty", "{prefix}<red>Inventory is not empty, Please clear up some room.");
}