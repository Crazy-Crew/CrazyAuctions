package com.badbones69.crazyauctions.api.registry.adapters;

import com.badbones69.crazyauctions.api.objects.items.PaperAuctionItem;
import com.ryderbelserion.fusion.core.api.FusionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.CrazyAuctions;
import us.crazycrew.api.constants.Messages;
import us.crazycrew.api.user.IPlayer;
import java.util.UUID;

public class PaperPlayerAdapter extends IPlayer<ItemStack, PaperAuctionItem> {

    protected FusionKey locale;
    protected Player player;

    public PaperPlayerAdapter(@Nullable final CommandSender sender) {
        if (sender instanceof Player reference) {
            this.player = reference;
        }
    }

    public PaperPlayerAdapter() {
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
    public void takeItem(@NonNull final PaperAuctionItem item) {
        final ItemStack itemStack = item.getItem();

        if (itemStack.isEmpty()) return;

        final PlayerInventory inventory = this.player.getInventory();

        inventory.remove(itemStack);
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