package com.badbones69.crazyauctions.api.frame.command;

import com.badbones69.crazyauctions.ApiManager;
import com.badbones69.crazyauctions.api.frame.command.builders.CommandActor;
import com.badbones69.crazyauctions.api.frame.command.builders.args.CommandArgs;
import com.badbones69.crazyauctions.config.types.Locale;
import com.badbones69.crazyauctions.frame.utils.AdventureUtils;
import com.badbones69.crazyauctions.support.PlaceholderSupport;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommandContext implements CommandActor, CommandArgs {

    private final Audience audience;
    private final List<String> args;
    private String label;

    private Player player;

    public CommandContext(Audience audience, String label, List<String> args) {
        this.audience = audience;

        if (audience instanceof Player) {
            this.player = (Player) audience;
        }

        this.label = label;
        this.args = args;
    }

    @Override
    public void reply(String message) {
        if (message.isBlank() || message.isEmpty()) return;

        Component component = AdventureUtils.parse(message);

        this.audience.sendMessage(component);
    }

    @Override
    public void reply(boolean hasPrefix, String prefix, String message) {
        if (message.isBlank() || message.isEmpty()) return;

        if (hasPrefix) {
            Component component = AdventureUtils.parse(prefix).append(AdventureUtils.parse(prefix));

            this.audience.sendMessage(component);

            return;
        }

        Component component = AdventureUtils.parse(message);

        this.audience.sendMessage(component);
    }

    @Override
    public void reply(boolean hasPrefix, String prefix, Component component) {
        if (hasPrefix) {
            this.audience.sendMessage(AdventureUtils.parse(prefix).append(component));
            return;
        }

        this.audience.sendMessage(component);
    }

    @Override
    public void reply(Component component) {
        this.audience.sendMessage(component);
    }

    @Override
    public void send(Audience audience, String message) {
        if (message.isBlank() || message.isEmpty()) return;

        Component component = AdventureUtils.parse(message);

        audience.sendMessage(component);
    }

    @Override
    public void send(Audience audience, Component component) {
        audience.sendMessage(component);
    }

    @Override
    public void send(Audience audience, String message, String prefix, boolean hasPrefix) {
        if (hasPrefix) {
            audience.sendMessage(AdventureUtils.parse(prefix).append(AdventureUtils.parse(message)));
            return;
        }

        send(audience, message);
    }

    @Override
    public void send(Audience audience, Component message, String prefix, boolean hasPrefix) {
        if (hasPrefix) {
            audience.sendMessage(AdventureUtils.parse(prefix).append(message));
            return;
        }

        send(audience, message);
    }

    @Override
    public Audience getSender() {
        return this.audience;
    }

    @Override
    public boolean isPlayer() {
        return getPlayer() != null;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.player.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(String rawPermission) {
        return this.player.hasPermission(rawPermission);
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public List<String> getArgs() {
        return Collections.unmodifiableList(this.args);
    }

    @Override
    public void removeArgs(int arg) {
        this.args.remove(arg);
    }

    @Override
    public int getArgAsInt(int index, boolean notifySender) {
        Integer value = null;

        try {
            value = Integer.parseInt(this.args.get(index));
        } catch (NumberFormatException exception) {
            if (notifySender) reply(PlaceholderSupport.setPlaceholders(ApiManager.getLocale().getProperty(Locale.INVALID_SYNTAX)
            .replaceAll("\\{value}", this.args.get(index))
            .replaceAll("\\{action}", "number")));
        }

        if (value != null) return value;

        return 1;
    }

    @Override
    public long getArgAsLong(int index, boolean notifySender) {
        Long value = null;

        try {
            value = Long.parseLong(this.args.get(index));
        } catch (NumberFormatException exception) {
            if (notifySender) reply(PlaceholderSupport.setPlaceholders(ApiManager.getLocale().getProperty(Locale.INVALID_SYNTAX)
                    .replaceAll("\\{value}", this.args.get(index))
                    .replaceAll("\\{action}", "number")));
        }

        if (value != null) return value;

        return 1L;
    }

    @Override
    public double getArgAsDouble(int index, boolean notifySender) {
        Double value = null;

        try {
            value = Double.parseDouble(this.args.get(index));
        } catch (NumberFormatException exception) {
            if (notifySender) reply(PlaceholderSupport.setPlaceholders(ApiManager.getLocale().getProperty(Locale.INVALID_SYNTAX)
                    .replaceAll("\\{value}", this.args.get(index))
                    .replaceAll("\\{action}", "number")));
        }

        if (value != null) return value;

        return 0.1;
    }

    @Override
    public boolean getArgAsBoolean(int index, boolean notifySender) {
        String lowercase = this.args.get(index).toLowerCase();

        switch (lowercase) {
            case "true", "on", "1" -> {
                return true;
            }
            case "false", "off", "0" -> {
                return false;
            }
            default -> {
                if (notifySender) reply(PlaceholderSupport.setPlaceholders(ApiManager.getLocale().getProperty(Locale.INVALID_SYNTAX)
                        .replaceAll("\\{value}", this.args.get(index).toLowerCase())
                        .replaceAll("\\{action}", "boolean")));

                return false;
            }
        }
    }

    @Override
    public float getArgAsFloat(int index, boolean notifySender) {
        Float value = null;

        try {
            value = Float.parseFloat(this.args.get(index));
        } catch (NumberFormatException exception) {
            if (notifySender) reply(PlaceholderSupport.setPlaceholders(ApiManager.getLocale().getProperty(Locale.INVALID_SYNTAX)
                    .replaceAll("\\{value}", this.args.get(index))
                    .replaceAll("\\{action}", "number")));
        }

        if (value != null) return value;

        return 1F;
    }

    @Override
    public Player getArgAsPlayer(int index, boolean notifySender) {
        Player player = Bukkit.getServer().getPlayer(this.args.get(index));

        if (player == null) {
            if (notifySender) reply(PlaceholderSupport.setPlaceholders(ApiManager.getLocale().getProperty(Locale.INVALID_SYNTAX)
                    .replaceAll("\\{value}", this.args.get(index))
                    .replaceAll("\\{action}", "player")));

            return null;
        }

        return player;
    }

    @Override
    public OfflinePlayer getArgAsOfflinePlayer(int index) {
        CompletableFuture<UUID> future = CompletableFuture.supplyAsync(() -> Bukkit.getServer().getOfflinePlayer(this.args.get(index))).thenApply(OfflinePlayer::getUniqueId);

        return Bukkit.getServer().getOfflinePlayer(future.join());
    }
}