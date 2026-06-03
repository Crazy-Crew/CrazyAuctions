package us.crazycrew.api.user;

import com.ryderbelserion.fusion.core.api.FusionKey;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public interface IUser {

    @NotNull UUID getUniqueId();

    @NotNull String getUsername();

    @NotNull FusionKey getLocaleKey();

    @ApiStatus.Internal
    void setLocale(@NotNull final String locale);

    default @NotNull String getLocale() {
        return getLocaleKey().asString();
    }
}