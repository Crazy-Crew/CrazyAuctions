package us.crazycrew.api.adapters;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.user.IPlayer;
import java.util.Optional;

public interface IPlayerAdapter<T> {

    @NotNull Optional<? extends IPlayer> getUser(@NotNull final T player);

}