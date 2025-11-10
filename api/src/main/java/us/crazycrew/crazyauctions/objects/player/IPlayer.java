package us.crazycrew.crazyauctions.objects.player;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.objects.items.IAuctionItem;
import java.util.Locale;
import java.util.UUID;

public abstract class IPlayer<A extends IAuctionItem<?>> {

    private final Audience player;
    private final Locale locale;
    private final String name;
    private final UUID uuid;

    public IPlayer(@NotNull final Audience player) {
        this.player = player;

        this.locale = this.player.get(Identity.LOCALE).orElse(Locale.ENGLISH);
        this.name = this.player.get(Identity.NAME).orElseThrow();
        this.uuid = this.player.get(Identity.UUID).orElseThrow();
    }

    public abstract void takeItem(@NotNull A item);

    public @NotNull Audience getPlayer() {
        return this.player;
    }

    public @NotNull Locale getLocale() {
        return this.locale;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull UUID getUuid() {
        return this.uuid;
    }
}