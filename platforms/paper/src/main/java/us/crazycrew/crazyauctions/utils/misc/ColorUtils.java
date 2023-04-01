package us.crazycrew.crazyauctions.utils.misc;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import us.crazycrew.crazyauctions.api.interfaces.Universal;
import us.crazycrew.crazyauctions.configurations.PluginSettings;
import us.crazycrew.crazyauctions.loader.AuctionsStarter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: Color utilities.
 */
public class ColorUtils implements Universal {

    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F\\d]{6}");

    public static String color(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.valueOf(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
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
        return switch (color.toUpperCase()) {
            case "AQUA" -> Color.AQUA;
            case "BLACK" -> Color.BLACK;
            case "BLUE" -> Color.BLUE;
            case "FUCHSIA" -> Color.FUCHSIA;
            case "GRAY" -> Color.GRAY;
            case "GREEN" -> Color.GREEN;
            case "LIME" -> Color.LIME;
            case "MAROON" -> Color.MAROON;
            case "NAVY" -> Color.NAVY;
            case "OLIVE" -> Color.OLIVE;
            case "ORANGE" -> Color.ORANGE;
            case "PURPLE" -> Color.PURPLE;
            case "RED" -> Color.RED;
            case "SILVER" -> Color.SILVER;
            case "TEAL" -> Color.TEAL;
            case "YELLOW" -> Color.YELLOW;
            default -> Color.WHITE;
        };
    }

    public static String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }

    public static String getPrefix() {
        return AuctionsStarter.getPluginConfig().getProperty(PluginSettings.COMMAND_PREFIX);
    }
}