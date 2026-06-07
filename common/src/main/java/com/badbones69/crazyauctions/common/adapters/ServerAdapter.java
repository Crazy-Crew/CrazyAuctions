package com.badbones69.crazyauctions.common.adapters;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.adapters.IServerAdapter;
import us.crazycrew.api.enums.server.ServerState;
import java.util.ArrayList;
import java.util.List;

public class ServerAdapter implements IServerAdapter {

    private final List<ServerState> states = new ArrayList<>();

    @Override
    public boolean hasState(@NonNull final ServerState state) {
        return this.states.contains(state);
    }

    @Override
    public void removeState(@NonNull final ServerState state) {
        this.states.remove(state);
    }

    @Override
    public void addState(@NonNull final ServerState state) {
        this.states.add(state);
    }
}