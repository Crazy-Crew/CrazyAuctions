package us.crazycrew.api.registry;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.user.IPlayer;
import java.util.Optional;
import java.util.UUID;

public interface IPlayerRegistry<S> {

    void init();

    IPlayer addUser(@NotNull final S player);

    IPlayer removeUser(@NotNull final UUID uuid);

    Optional<? extends IPlayer> getUser(@NotNull final UUID uuid);

    IPlayer getConsole();

}