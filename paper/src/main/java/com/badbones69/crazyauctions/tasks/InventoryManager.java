package com.badbones69.crazyauctions.tasks;

import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.api.guis.Holder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class InventoryManager {

    private static ItemBuilder nextButton;
    private static ItemBuilder backButton;

    public static void loadButtons() {
        FileConfiguration config = Files.config.getConfiguration();

        String next = "Settings.GUISettings.OtherSettings.NextPage";

        nextButton = new ItemBuilder().setMaterial(config.getString(next + ".Item"))
                .setName(config.getString(next + ".Name"))
                .setLore(config.getStringList(next + ".Lore"))
                .addString("NextPage", Keys.auction_button.getNamespacedKey());

        final String back = "Settings.GUISettings.OtherSettings.PreviousPage";

        backButton = new ItemBuilder().setMaterial(config.getString(back + ".Item"))
                .setName(config.getString(back + ".Name"))
                .setLore(config.getStringList(back + ".Lore"))
                .addString("PreviousPage", Keys.auction_button.getNamespacedKey());
    }

    public static ItemBuilder getNextButton(final Player player, final Holder holder) {
        return nextButton.addLorePlaceholder("{page}", String.valueOf(holder.getPage() - 1)).setTarget(player);
    }

    public static ItemBuilder getBackButton(final Player player, final Holder holder) {
        return backButton.addLorePlaceholder("{page}", String.valueOf(holder.getPage() - 1)).setTarget(player);
    }
}