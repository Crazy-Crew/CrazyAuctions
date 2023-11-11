package com.badbones69.crazyauctions.api.enums;

import org.bukkit.permissions.PermissionDefault;

public enum Permissions {

    PLAYER_HELP("player.help", "Gives access to the help command.", PermissionDefault.TRUE);

    private final String node;
    private final String description;
    private final PermissionDefault permissionDefault;

    Permissions(String node, String description, PermissionDefault permissionDefault) {
        this.node = node;
        this.description = description;
        this.permissionDefault = permissionDefault;
    }

    public String getNode() {
        return this.node;
    }

    public String getDescription() {
        return this.description;
    }

    public PermissionDefault getPermissionDefault() {
        return this.permissionDefault;
    }

    public String getBuiltPermission(String action) {
        return "crazyauctions." + action + "." + this.node;
    }

    public String getBuiltPermission() {
        return "crazyauctions.command." + this.node;
    }
}