package com.badbones69.crazyauctions.commands.features.admin.migrate;

import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyauctions.commands.features.admin.migrate.enums.MigrationType;
import com.badbones69.crazyauctions.commands.features.admin.migrate.interfaces.types.AuctionsDeprecated;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;
import java.util.Optional;

public class CommandMigrate extends BaseCommand {

    @Command(value = "migrate")
    @Permission(value = "crazyauctions.migrate", def = PermissionDefault.TRUE)
    @Syntax("/crazyauctions migrate -mt [type]")
    @Flag(flag = "mt", longFlag = "migration_type", argument = String.class, suggestion = "migrators")
    public void migrate(final CommandSender sender, final Flags flags) {
        final boolean hasFlag = flags.hasFlag("mt");

        if (!hasFlag) {
            Messages.LACKING_FLAG.sendMessage(sender, new HashMap<>() {{
                put("{flag}", "-mt");
                put("{usage}", "/crazyauctions migrate -mt [type]");
            }});

            return;
        }

        final Optional<String> key = flags.getFlagValue("mt");

        if (key.isEmpty()) {
            Messages.MIGRATION_NOT_AVAILABLE.sendMessage(sender);

            return;
        }

        final MigrationType type = MigrationType.fromName(key.get());

        if (type == null) {
            Messages.MIGRATION_NOT_AVAILABLE.sendMessage(sender);

            return;
        }

        switch (type) {
            case AUCTIONS_CONFIG, AUCTIONS_DATA -> new AuctionsDeprecated(sender, type).run();
        }
    }
}