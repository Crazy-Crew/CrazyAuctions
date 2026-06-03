package us.crazycrew.api.adapters;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.user.IUser;
import java.util.Optional;

public interface IPlayerAdapter<T> {

    @NotNull Optional<? extends IUser> getUser(@NotNull final T player);

}