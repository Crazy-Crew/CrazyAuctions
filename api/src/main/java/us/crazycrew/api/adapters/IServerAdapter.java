package us.crazycrew.api.adapters;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.enums.server.ServerState;

public interface IServerAdapter {

    boolean hasState(@NonNull final ServerState state);

    void removeState(@NonNull final ServerState state);

    void addState(@NonNull final ServerState state);

}