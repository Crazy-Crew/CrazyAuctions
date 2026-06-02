package com.badbones69.crazyauctions.controllers;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import com.ryderbelserion.fusion.paper.builders.folia.Scheduler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TrafficListener implements Listener {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final FusionPaper fusion = this.plugin.getFusion();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final YamlConfiguration configuration = Files.config.getConfiguration();

        final boolean isRepeatingMessage = configuration.getBoolean("Settings.Repeating-Message.Toggle", false);

        if (!isRepeatingMessage) {
            this.fusion.log(Level.WARNING, "The toggle for repeating messages is not enabled!");

            return;
        }

        final String message = configuration.getString("Settings.Repeating-Message.Message", "");

        if (message.isBlank()) {
            this.fusion.log(Level.WARNING, "The message to be sent with repeating messages is blank!");

            return;
        }

        final int interval = configuration.getInt("Settings.Repeating-Message.Interval", 0);

        final YamlConfiguration data = Files.data.getConfiguration();

        final ConfigurationSection section = data.getConfigurationSection("OutOfTime/Cancelled");

        if (section == null) {
            this.fusion.log(Level.WARNING, "The configuration section required in data.yml is not present!");

            return;
        }

        boolean hasPlayer = false;

        final String uuid = player.getUniqueId().toString();

        for (final String id : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(id);

            if (item == null) {
                this.fusion.log(Level.WARNING, "Item cannot be null for id %s!", id);

                continue;
            }

            final String seller = item.getString("Seller", "");

            if (seller.isBlank()) {
                this.fusion.log(Level.WARNING, "The seller for id %s cannot be blank!", id);

                continue;
            }

            hasPlayer = seller.equals(uuid);

            if (hasPlayer) {
                break;
            }
        }

        if (!hasPlayer) return;

        if (interval > 0) {
            new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
                @Override
                public void run() {
                    player.sendMessage(Methods.color(message));
                }
            }.runAtFixedRate(0, interval);

            return;
        }

        new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
            @Override
            public void run() {
                player.sendMessage(Methods.color(message));
            }
        }.runNextTick();
    }
}