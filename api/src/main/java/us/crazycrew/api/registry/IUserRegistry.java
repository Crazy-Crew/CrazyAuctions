package us.crazycrew.api.registry;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.user.IUser;
import java.util.Optional;
import java.util.UUID;

public interface IUserRegistry<S> {

    void init();

    IUser addUser(@NotNull final S player);

    IUser removeUser(@NotNull final UUID uuid);

    Optional<? extends IUser> getUser(@NotNull final UUID uuid);

    IUser getConsole();

}