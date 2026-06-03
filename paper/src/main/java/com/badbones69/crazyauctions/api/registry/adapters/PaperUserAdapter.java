package com.badbones69.crazyauctions.api.registry.adapters;

import com.ryderbelserion.fusion.core.api.FusionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.api.CrazyAuctions;
import us.crazycrew.api.constants.Messages;
import us.crazycrew.api.user.IUser;
import java.util.UUID;

public class PaperUserAdapter implements IUser {

    protected FusionKey locale;
    protected Player player;

    public PaperUserAdapter(@Nullable final CommandSender sender) {
        if (sender instanceof Player reference) {
            this.player = reference;
        }
    }

    public PaperUserAdapter() {
        this(null);
    }

    @Override
    public @NotNull final UUID getUniqueId() {
        return this.player == null ? CrazyAuctions.CONSOLE_UUID : this.player.getUniqueId();
    }

    @Override
    public @NotNull final String getUsername() {
        return this.player == null ? CrazyAuctions.CONSOLE_NAME : this.player.getName();
    }

    @Override
    public @NotNull final FusionKey getLocaleKey() {
        //return this.player == null ? Messages.default_locale : this.locale;
        return Messages.default_locale;
    }

    @Override
    public void setLocale(@NotNull final String locale) {
        final String[] splitter = locale.contains("-") ? locale.split("-") : locale.split("_");

        final String language = splitter[0];
        final String country = splitter[1];

        final String value = "%s_%s.yml".formatted(language, country).toLowerCase();

        if (!value.equalsIgnoreCase("en_us.yml")) {
            this.locale = FusionKey.key(CrazyAuctions.namespace, value);
        }
    }
}