package com.badbones69.crazyauctions.commands.features.admin.migrate.interfaces;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.commands.features.admin.migrate.enums.MigrationType;
import com.ryderbelserion.fusion.core.api.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public abstract class IAuctionMigrator {

    protected final CrazyAuctions plugin = CrazyAuctions.get();

    protected final FusionPaper fusion = this.plugin.getFusion();

    protected final CommandSender sender;
    protected final MigrationType type;
    protected final long startTime;

    public IAuctionMigrator(@NotNull final CommandSender sender, @NotNull final MigrationType type) {
        this.startTime = System.nanoTime();
        this.sender = sender;
        this.type = type;
    }

    public abstract void run();

    public void sendMessage(@NotNull final List<String> files, final List<String> auctions, final int success, final int failed) {
        Messages.MIGRATION_SUCCESS.sendMessage(this.sender, new HashMap<>() {{
            if (files.size() > 1) {
                put("%files%", StringUtils.toString(files));
            } else {
                put("%files%", files.getFirst());
            }

            if (!auctions.isEmpty()) {
                put("%auctions%", auctions.toString().replace("[", "").replace("]", ""));
            }

            put("%succeeded_amount%", String.valueOf(success));
            put("%failed_amount%", String.valueOf(failed));
            put("%type%", type.getName());
            put("%time%", time());
        }});
    }

    public @NotNull final String time() {
        final double time = (double) (System.nanoTime() - this.startTime) / 1.0E9D;

        return String.format(Locale.ROOT, "%.3fs", time);
    }

}