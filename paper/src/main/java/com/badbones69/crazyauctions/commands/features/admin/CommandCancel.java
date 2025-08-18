package com.badbones69.crazyauctions.commands.features.admin;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.Reasons;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandCancel extends BaseCommand {

    @Command(value = "cancel", alias = "force_end_all")
    @Permission(value = "crazyauctions.cancel", def = PermissionDefault.TRUE)
    @Syntax("/crazyauctions cancel/force_end_all")
    public void cancel(final Player player) {
        final FileConfiguration data = Files.data.getConfiguration();

        final ConfigurationSection section = data.getConfigurationSection("Items");

        if (section == null) return;

        int number = 1;

        for (final String item : section.getKeys(false)) {
            final ConfigurationSection user = section.getConfigurationSection(item);

            if (user == null) continue;

            final String id = user.getString("Seller", "Seller field not found in data.yml for %s".formatted(item));

            final OfflinePlayer offlinePlayer = Methods.getOfflinePlayer(id);

            if (offlinePlayer instanceof Player human) {
                human.sendMessage(Messages.ADMIN_FORCE_CANCELLED_TO_PLAYER.getMessage(human));
            }

            number = Methods.expireItem(number, offlinePlayer, item, data, Reasons.ADMIN_FORCE_CANCEL);
        }

        Files.data.save();

        player.sendMessage(Messages.ADMIN_FORCE_CANCELLED_ALL.getMessage(player));
    }
}