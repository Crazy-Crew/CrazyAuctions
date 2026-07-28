package com.badbones69.crazyauctions.api.registry;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.CrazyAuctionsPaper;
import com.badbones69.crazyauctions.api.builders.gui.buttons.AbstractButton;
import com.badbones69.crazyauctions.api.builders.gui.buttons.types.GenericButton;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import com.badbones69.crazyauctions.common.enums.keys.FileKeys;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PaperButtonRegistry {

    private final CrazyAuctions plugin = CrazyAuctions.get();
    private final CrazyAuctionsPaper platform = this.plugin.getPlatform();
    private final FusionPaper fusion = this.platform.getFusion();

    private final Map<String, AbstractButton> buttons = new HashMap<>();

    public void init() {
        final YamlConfiguration configuration = FileKeys.config.getConfiguration();

        populate(configuration.getConfigurationSection("Settings.GUISettings.OtherSettings"));
        populate(configuration.getConfigurationSection("Settings.GUISettings.Category-Settings"));
    }

    public void populate(final ConfigurationSection section) {
        if (section == null) {
            return;
        }

        for (final String option : section.getKeys(false)) {
            final ConfigurationSection index = section.getConfigurationSection(option);

            if (index == null) {
                continue;
            }

            switch (option) {
                case "Bidding/Selling", "WhatIsThis" -> {
                    for (final String id : index.getKeys(false)) {
                        final ConfigurationSection level = index.getConfigurationSection(id);

                        if (level == null) {
                            continue;
                        }

                        addButton("%s.%s".formatted(option, id), level);
                    }
                }

                default -> addButton(option, index);
            }
        }
    }

    public void addButton(final String option, final ConfigurationSection index) {
        if (!index.getBoolean("Toggle", true)) {
            return;
        }

        final String id = index.getString("Item", "");

        if (id.isEmpty()) {
            return;
        }

        this.fusion.log(Level.INFO, "<yellow>Registering</yellow> <red>%s <yellow>option as a gui button!</yellow>", option);

        this.buttons.putIfAbsent(option, new GenericButton(
                id,
                index.getString("Name", ""),
                option,
                index.getStringList("Lore"),
                index.getInt("Slot", -1),
                Keys.auction_button
        ));
    }

    public void reload() {
        this.buttons.clear();

        init();
    }

    public Optional<AbstractButton> getButtonByName(final String id) {
        return Optional.ofNullable(this.buttons.get(id));
    }
}