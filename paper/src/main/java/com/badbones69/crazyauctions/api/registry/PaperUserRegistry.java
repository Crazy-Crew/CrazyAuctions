package com.badbones69.crazyauctions.api.registry;

import com.badbones69.crazyauctions.api.registry.adapters.PaperUserAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.CrazyAuctions;
import us.crazycrew.api.registry.IUserRegistry;
import us.crazycrew.api.user.IUser;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PaperUserRegistry implements IUserRegistry<Player> {

    private final Map<UUID, PaperUserAdapter> users = new HashMap<>();

    @Override
    public void init() {
        this.users.put(CrazyAuctions.CONSOLE_UUID, new PaperUserAdapter());
    }

    @Override
    public PaperUserAdapter addUser(@NonNull final Player player) {
        final String locale = player.locale().toString();

        final UUID uuid = player.getUniqueId();

        final PaperUserAdapter user = new PaperUserAdapter(player);

        user.setLocale(locale);

        this.users.putIfAbsent(uuid, user);

        return user;
    }

    @Override
    public PaperUserAdapter removeUser(@NotNull final UUID uuid) {
        return this.users.remove(uuid);
    }

    @Override
    public Optional<PaperUserAdapter> getUser(@NotNull UUID uuid) {
        return Optional.of(this.users.get(uuid));
    }

    @Override
    public @NotNull final IUser getConsole() {
        return this.users.get(CrazyAuctions.CONSOLE_UUID);
    }
}