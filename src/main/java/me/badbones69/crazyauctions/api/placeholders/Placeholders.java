package me.badbones69.crazyauctions.api.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Placeholders {
    private static Placeholders instance;
    private final List<Placeholder> placeholderList = new ArrayList<>();

    private Placeholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderList.add(new PlaceholderAPI());
        }
    }

    public static Placeholders getInstance() {
        if (instance == null) {
            instance = new Placeholders();
        }

        return instance;
    }

    /**
     * Adds a new placeholder to the list of placeholders
     * @param placeholder
     */
    public void addPlaceholder(Placeholder placeholder) {
        placeholderList.add(placeholder);
    }

    /**
     * Runs all the placeholders for the given player and message
     * @param player
     * @param message
     * @return
     */
    public String replace(Player player, String message) {
        return runPlaceholders(placeholderList, player, message);
    }

    /**
     * Runs all the placeholders for the given player and message.
     * Allows a list of placeholders to be used for this only time
     * which are consumed before the basic ones.
     * @param placeholders
     * @param player
     * @param message
     * @return
     */
    public String replace(List<Placeholder> placeholders, Player player, String message) {
        List<Placeholder> p = new ArrayList<>(placeholders);
        p.addAll(placeholderList);

        return runPlaceholders(p, player, message);
    }

    /**
     * Runs all the placeholders for the given player and message.
     * Allows a placeholder to be used for this only time
     * which is consumed before the basic ones.
     * @param placeholder
     * @param player
     * @param message
     * @return
     */
    public String replace(Placeholder placeholder, Player player, String message) {
        List<Placeholder> p = new ArrayList();
        p.add(placeholder);
        p.addAll(placeholderList);

        return runPlaceholders(p, player, message);
    }

    private String runPlaceholders(List<Placeholder> placeholders, Player player, String message) {
        String output = message;

        for (Placeholder p: placeholders) {
            output = p.replace(player, output);
        }

        return output;
    }
}
