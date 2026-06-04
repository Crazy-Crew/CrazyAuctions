package com.badbones69.crazyauctions.commands.admin.migrate.types;

import com.badbones69.crazyauctions.commands.admin.migrate.interfaces.IAuctionMigrator;
import com.badbones69.crazyauctions.common.enums.FileKey;
import com.badbones69.crazyauctions.common.enums.MigrationKey;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.message.adapter.interfaces.IMessageAdapter;
import com.ryderbelserion.fusion.kyori.utils.AdvUtils;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import java.util.Optional;

public class LegacyMessagesType extends IAuctionMigrator {

    public LegacyMessagesType(@NonNull final CommandSender sender) {
        super(sender, MigrationKey.legacy_messages);
    }

    @Override
    public void run() {
        final CommentedConfigurationNode configuration = FileKey.messages.getYamlConfig();

        final CommentedConfigurationNode messages = configuration.node("Messages");

        messages.childrenMap().forEach((id, _) -> {
            final Optional<IMessageAdapter> adapter = this.messageRegistry.getMessage(FusionKey.key(this.fusion.getNamespace(), String.valueOf(id).toLowerCase().replace("-", "_")));

            final CommentedConfigurationNode section = messages.node(id);

            try {
                section.set(String.class, AdvUtils.convert(section.getString(adapter.map(IMessageAdapter::getValue).orElse(""))));
            } catch (final SerializationException exception) {
                exception.printStackTrace();
            }
        });

        FileKey.messages.save();

        this.messageImpl.reload();
    }
}