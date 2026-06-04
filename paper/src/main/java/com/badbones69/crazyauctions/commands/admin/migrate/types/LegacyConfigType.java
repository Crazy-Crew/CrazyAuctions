package com.badbones69.crazyauctions.commands.admin.migrate.types;

import com.badbones69.crazyauctions.commands.admin.migrate.interfaces.IAuctionMigrator;
import com.badbones69.crazyauctions.common.enums.FileKey;
import com.badbones69.crazyauctions.common.enums.MigrationKey;
import com.ryderbelserion.fusion.kyori.utils.AdvUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

public class LegacyConfigType extends IAuctionMigrator {

    public LegacyConfigType(@NonNull final CommandSender sender) {
        super(sender, MigrationKey.legacy_config);
    }

    @Override
    public void run() {
        final YamlConfiguration configuration = FileKey.config.getConfiguration();

        configuration.set("Settings.Prefix", AdvUtils.convert(configuration.getString("Settings.Prefix", "<gray>[<dark_red>Crazy <aqua>Auctions<gray>]: ")));

        FileKey.config.save();
    }
}