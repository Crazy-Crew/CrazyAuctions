package com.badbones69.crazyauctions.config.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;

public class Locale implements SettingsHolder {

    public Locale() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Submit your translations here: https://github.com/Crazy-Crew/CrazyAuctions/discussions/categories/translations",
                "",
                "Legacy color codes such as &7,&c no longer work. You must use MiniMessage",
                "https://docs.advntr.dev/minimessage/format.html#color"
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };

        conf.setComment("general", header);
    }

    public static final Property<String> UNKNOWN_COMMAND = PropertyInitializer.newProperty("general.unknown-command", "{prefix}<red>The command {command} is not known.</red>");

    public static final Property<String> INVALID_SYNTAX = PropertyInitializer.newProperty("general.invalid-syntax", "{prefix}<gold>{value}</gold> <red>is an invalid {action}.</red>");

    public static final Property<String> NO_PERMISSION = PropertyInitializer.newProperty("general.no-permission", "{prefix}<red>You do not have permission to use that command!</red>");

    public static final Property<String> REQUIRED_ARGUMENT = PropertyInitializer.newProperty("general.checks.required-argument", "{prefix}<red>This argument is not optional</red>");

    public static final Property<String> OPTIONAL_ARGUMENT = PropertyInitializer.newProperty("general.checks.optional-argument", "{prefix}<green>This argument is optional</green>");

    public static final Property<String> NOT_ENOUGH_ARGS = PropertyInitializer.newProperty("general.checks.not-enough-args", "{prefix}<red>You did not supply enough arguments.</red>");

    public static final Property<String> TOO_MANY_ARGS = PropertyInitializer.newProperty("general.checks.too-many-args", "{prefix}<red>You put more arguments then I can handle.</red>");

    public static final Property<String> MUST_BE_PLAYER = PropertyInitializer.newProperty("general.player-checks.must-be-player", "{prefix}<red>You must be a player to use this command.</red>");

}