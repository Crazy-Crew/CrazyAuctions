package com.badbones69.crazyauctions.api.frame.command.builders;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.List;

public interface CommandActor {

    void reply(String message);

    void reply(boolean hasPrefix, String prefix, String message);

    void reply(boolean hasPrefix, String prefix, Component component);

    void reply(Component component);

    void send(Audience audience, String message);

    void send(Audience audience, Component component);

    void send(Audience audience, String message, String prefix, boolean hasPrefix);

    void send(Audience audience, Component message, String prefix, boolean hasPrefix);

    Audience getSender();

    boolean isPlayer();

    Player getPlayer();

    boolean hasPermission(Permission permission);

    boolean hasPermission(String rawPermission);

    void setLabel(String alias);

    String getLabel();

    List<String> getArgs();

    void removeArgs(int arg);

    //CommandHelpEntry getHelpEntry();

}