package us.crazycrew.api.user;

import com.ryderbelserion.fusion.core.api.FusionKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.items.IAuctionItem;
import java.util.UUID;

public abstract class IPlayer<I, A extends IAuctionItem<I>> {

    public abstract @NonNull UUID getUniqueId();

    public abstract @NonNull String getUsername();

    public abstract @NonNull FusionKey getLocaleKey();

    public abstract void takeItem(@NonNull final A item);

    @ApiStatus.Internal
    public abstract void setLocale(@NotNull final String locale);

    public @NonNull String getLocale() {
        return getLocaleKey().asString();
    }
}