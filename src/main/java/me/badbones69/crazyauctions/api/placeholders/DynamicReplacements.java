package me.badbones69.crazyauctions.api.placeholders;

import org.bukkit.entity.Player;

import java.util.Map;

public class DynamicReplacements implements Placeholder {
    private Map<String, String> replacements;

    public DynamicReplacements(Map<String, String> replacements) {
        this.replacements = replacements;
    }

    public String replace(Player player, String message) {
        String output = message;

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            output = output.replace(entry.getKey(), entry.getValue());
        }

        return output;
    }
}
