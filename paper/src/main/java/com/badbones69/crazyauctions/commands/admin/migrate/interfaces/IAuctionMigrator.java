package com.badbones69.crazyauctions.commands.admin.migrate.interfaces;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.CrazyAuctionsPaper;
import com.badbones69.crazyauctions.api.registry.adapters.PaperSenderAdapter;
import com.badbones69.crazyauctions.common.enums.MigrationKey;
import com.badbones69.crazyauctions.common.enums.messages.Messages;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class IAuctionMigrator {

    protected final CrazyAuctions plugin = CrazyAuctions.get();

    protected final CrazyAuctionsPaper platform = this.plugin.getPlatform();

    protected final FusionPaper fusion = this.platform.getFusion();

    protected final MessageRegistry messageRegistry = this.fusion.getMessageRegistry();

    protected final PaperSenderAdapter adapter = this.platform.getSenderAdapter();

    protected final CommandSender sender;
    protected final MigrationKey key;
    protected final long startTime;

    public IAuctionMigrator(@NonNull final CommandSender sender, @NonNull final MigrationKey key) {
        this.startTime = System.nanoTime();
        this.sender = sender;
        this.key = key;
    }

    public abstract void run();

    public void sendMessage(
            @NonNull final List<String> files,
            @NonNull final List<String> auctions, final int success, final int failed
    ) {
        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{files}", files.size() > 1 ? StringUtils.toString(files) : files.getFirst());

        if (!auctions.isEmpty()) {
            placeholders.put("{auctions}", auctions.toString().replace("[", "").replace("]", ""));
        }

        placeholders.put("{succeeded_amount}", String.valueOf(success));
        placeholders.put("{failed_amount}", String.valueOf(failed));
        placeholders.put("{type}", this.key.getName());
        placeholders.put("{time}", time());

        Messages.migration_success.sendMessage(this.sender, placeholders);
    }

    public @NonNull final String time() {
        final double time = (double) (System.nanoTime() - this.startTime) / 1.0E9D;

        return String.format(Locale.ROOT, "%.3fs", time);
    }
}