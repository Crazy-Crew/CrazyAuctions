package com.badbones69.crazyauctions.api.registry;

import com.badbones69.crazyauctions.api.registry.adapters.PaperPlayerAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.CrazyAuctions;
import us.crazycrew.api.registry.IPlayerRegistry;
import us.crazycrew.api.user.IPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PaperUserRegistry implements IPlayerRegistry<Player> {

    private final Map<UUID, PaperPlayerAdapter> users = new HashMap<>();

    @Override
    public void init() {
        this.users.put(CrazyAuctions.CONSOLE_UUID, new PaperPlayerAdapter());
    }

    @Override
    public PaperPlayerAdapter addUser(@NonNull final Player player) {
        final String locale = player.locale().toString();

        final UUID uuid = player.getUniqueId();

        final PaperPlayerAdapter user = new PaperPlayerAdapter(player);

        user.setLocale(locale);

        this.users.putIfAbsent(uuid, user);

        return user;
    }

    @Override
    public PaperPlayerAdapter removeUser(@NotNull final UUID uuid) {
        return this.users.remove(uuid);
    }

    @Override
    public Optional<PaperPlayerAdapter> getUser(@NotNull UUID uuid) {
        return Optional.of(this.users.get(uuid));
    }

    @Override
    public @NotNull final IPlayer getConsole() {
        return this.users.get(CrazyAuctions.CONSOLE_UUID);
    }
}