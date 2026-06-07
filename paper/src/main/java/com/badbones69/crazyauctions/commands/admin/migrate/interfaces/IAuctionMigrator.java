package com.badbones69.crazyauctions.commands.admin.migrate.interfaces;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.CrazyPlatform;
import com.badbones69.crazyauctions.api.registry.adapters.PaperSenderAdapter;
import com.badbones69.crazyauctions.common.enums.MigrationKey;
import com.badbones69.crazyauctions.common.registry.MessageImpl;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.kyori.utils.AdvUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Locale;

public abstract class IAuctionMigrator {

    protected final CrazyAuctions plugin = CrazyAuctions.get();

    protected final CrazyPlatform platform = this.plugin.getPlatform();

    protected final FusionPaper fusion = this.platform.getFusion();

    protected final MessageRegistry messageRegistry = this.fusion.getMessageRegistry();

    protected final MessageImpl messageImpl = this.platform.getMessageImpl();

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
        /*Messages.MIGRATION_SUCCESS.sendMessage(this.sender, new HashMap<>() {{
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
        }});*/
    }

    public void color(@NonNull final CommentedConfigurationNode configuration, @NonNull final String file, @NonNull final String value, @NonNull final Object... path) {
        set(configuration, file, AdvUtils.convert(value, true), path);
    }

    public void set(@NonNull final CommentedConfigurationNode configuration, @NonNull final String file, @NonNull final String value, @NonNull final Object... path) {
        try {
            configuration.node(path).set(String.class, value);
        } catch (final SerializationException exception) {
            this.fusion.log(Level.WARNING, "Failed to set to path %s in file %s", exception, path, file);
        }
    }

    public @NonNull final String time() {
        final double time = (double) (System.nanoTime() - this.startTime) / 1.0E9D;

        return String.format(Locale.ROOT, "%.3fs", time);
    }
}