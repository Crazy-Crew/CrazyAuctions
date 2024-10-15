package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.objects.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    private final Set<User> users = new HashSet<>();

    public UserManager(final CrazyAuctions plugin) {
        this.users.add(new User(plugin.getServer().getConsoleSender()));
    }

    public void addUser(final Player player) {
        this.users.add(new User(player));
    }

    public void removeUser(final Player player) {
        final User user = getUser(player);

        if (user == null) return;

        this.users.remove(user);
    }

    public final User getUser(final CommandSender sender) {
        if (sender instanceof Player player) {
            return getUser(player);
        }

        User user = null;

        final String name = sender.getName();

        for (final User key : this.users) {
            if (name.equals(key.sender.getName())) {
                user = key;

                break;
            }
        }

        return user;
    }

    public final User getUser(final Player player) {
        User user = null;

        final UUID uuid = player.getUniqueId();

        for (final User key : this.users) {
            if (key.player == null) continue;

            if (uuid.equals(key.player.getUniqueId())) {
                user = key;

                break;
            }
        }

        return user;
    }

    public void purge() {
        this.users.clear();
    }
}