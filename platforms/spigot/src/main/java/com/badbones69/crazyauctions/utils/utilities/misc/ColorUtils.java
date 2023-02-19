package com.badbones69.crazyauctions.utils.utilities.misc;

import com.badbones69.crazyauctions.api.enums.ServerVersion;
import com.badbones69.crazyauctions.configs.Config;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F\\d]{6}");

    public static String color(String message) {
        if (ServerVersion.isAtLeast(ServerVersion.v1_15)) {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer buffer = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(buffer, ChatColor.valueOf(matcher.group()).toString());
            }

            return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void color(List<Color> colors, String colorString) {
        if (colorString.contains(", ")) {
            for (String color : colorString.split(", ")) {
                Color c = getColor(color);

                if (c != null) colors.add(c);
            }
        } else {
            Color c = getColor(colorString);

            if (c != null) colors.add(c);
        }
    }

    public static Color getColor(String color) {
        switch (color.toUpperCase()) {
            case "AQUA":
                return Color.AQUA;
            case "BLACK":
                return Color.BLACK;
            case "BLUE":
                return Color.BLUE;
            case "FUCHSIA":
                return Color.FUCHSIA;
            case "GRAY":
                return Color.GRAY;
            case "GREEN":
                return Color.GREEN;
            case "LIME":
                return Color.LIME;
            case "MAROON":
                return Color.MAROON;
            case "NAVY":
                return Color.NAVY;
            case "OLIVE":
                return Color.OLIVE;
            case "ORANGE":
                return Color.ORANGE;
            case "PURPLE":
                return Color.PURPLE;
            case "RED":
                return Color.RED;
            case "SILVER":
                return Color.SILVER;
            case "TEAL":
                return Color.TEAL;
            case "YELLOW":
                return Color.YELLOW;
        }

        return Color.WHITE;
    }

    public static String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }

    public static String getPrefix(String string) {
        return Config.PREFIX;
    }

    public static String getPrefix() {
        return getPrefix("");
    }
}