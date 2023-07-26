package com.badbones69.crazyauctions.api.frame.command.builders.reqs;

import org.bukkit.permissions.Permission;

public class CommandRequirementsBuilder {

    private boolean asPlayer = false;
    private Permission permission = null;
    private String rawPermission = "";

    public CommandRequirementsBuilder asPlayer(boolean value) {
        this.asPlayer = value;
        return this;
    }

    public CommandRequirementsBuilder withPermission(Permission permission) {
        this.permission = permission;
        return this;
    }

    public CommandRequirementsBuilder withOutPermission(Permission permission) {
        return this;
    }

    public CommandRequirementsBuilder withRawPermission(String rawPermission) {
        this.rawPermission = rawPermission;
        return this;
    }

    public CommandRequirements build() {
        return new CommandRequirements(asPlayer, permission, rawPermission);
    }
}