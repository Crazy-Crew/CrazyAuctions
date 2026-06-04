package com.badbones69.crazyauctions.commands.arguments;

import com.badbones69.crazyauctions.common.enums.MigrationKey;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jspecify.annotations.NonNull;
import java.util.concurrent.CompletableFuture;

public class MigrationArgument implements CustomArgumentType.Converted<MigrationKey, String> {

    private final FusionPaper fusion = (FusionPaper) FusionProvider.getInstance();

    @Override
    public @NonNull final MigrationKey convert(@NonNull final String nativeType) throws CommandSyntaxException {
        return MigrationKey.fromName(nativeType);
    }

    @Override
    public @NonNull final <S> CompletableFuture<Suggestions> listSuggestions(@NonNull final CommandContext<S> context, @NonNull final SuggestionsBuilder builder) {
        for (final MigrationKey migration : MigrationKey.values()) {
            final String name = migration.getName();

            if (name.equalsIgnoreCase("none")) continue;

            if (!name.startsWith(builder.getRemainingLowerCase())) continue;

            builder.suggest(name, MessageComponentSerializer.message().serialize(this.fusion.asComponent(migration.getDesc())));
        }

        return builder.buildFuture();
    }

    @Override
    public @NonNull final ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}