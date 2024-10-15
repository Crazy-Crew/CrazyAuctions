package com.badbones69.crazyauctions.api.builders.gui;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.configs.ConfigManager;
import com.badbones69.crazyauctions.configs.enums.Files;
import com.ryderbelserion.vital.paper.api.enums.Support;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public abstract class InventoryBuilder {

    protected final CrazyAuctions plugin = CrazyAuctions.getPlugin();
    protected final CrazyManager crazyManager = this.plugin.getCrazyManager();
    protected final Server server = this.plugin.getServer();

    protected final SettingsManager config = ConfigManager.getConfig();

    protected final SettingsManager auctions = ConfigManager.getCustomConfig(Files.auctions.getFileName());

    protected final Player player;

    public InventoryBuilder(final Player player) {
        this.player = player;
    }

    public final String parse(final Player player, final String title) {
        return Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, title) : title;
    }
}