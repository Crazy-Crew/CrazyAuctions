package com.badbones69.crazyauctions.commands.admin.migrate.types;

import com.badbones69.crazyauctions.commands.admin.migrate.interfaces.IAuctionMigrator;
import com.badbones69.crazyauctions.common.enums.FileKey;
import com.badbones69.crazyauctions.common.enums.MigrationKey;
import com.ryderbelserion.fusion.kyori.utils.AdvUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class LegacyConfigType extends IAuctionMigrator {

    public LegacyConfigType(@NonNull final CommandSender sender) {
        super(sender, MigrationKey.legacy_config);
    }

    @Override
    public void run() {
        final YamlConfiguration configuration = FileKey.config.getConfiguration();

        final ConfigurationSection section = configuration.getConfigurationSection("Settings");

        if (section == null) {
            return;
        }

        section.set("Prefix", AdvUtils.convert(section.getString("Prefix", "<gray>[<dark_red>Crazy <aqua>Auctions<gray>]: "), true));

        final CommentedConfigurationNode auction = FileKey.auction.getYamlConfig();

        boolean isSave = false;

        if (section.contains("GUIName")) {
            color(auction, "auction.yml", section.getString("GUIName", "<red>Crazy<dark_blue>Auctions"), "settings", "gui", "name");

            isSave = true;
        }

        if (isSave) {
            FileKey.auction.save();
        }

        FileKey.config.save();
    }
}