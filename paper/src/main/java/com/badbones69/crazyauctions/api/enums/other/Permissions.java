package com.badbones69.crazyauctions.api.enums.other;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import java.util.HashMap;
import java.util.Map;

public enum Permissions {

    reload("reload", "Access to /crazyauctions reload", PermissionDefault.OP, false),
    help("help", "Access to /crazyauctions help", PermissionDefault.TRUE, false),
    view("view", "Access to /crazyauctions view", PermissionDefault.TRUE, false),
    expired("expired", "Access to /crazyauctions expired", PermissionDefault.TRUE, false),
    listed("listed", "Access to /crazyauctions listed", PermissionDefault.TRUE, false),
    sell("sell", "Access to /crazyauctions sell", PermissionDefault.TRUE, false),
    bid("bid", "Access to /crazyauctions bid", PermissionDefault.TRUE, false),
    use("use", "Access to /crazyauctions", PermissionDefault.TRUE, false),

    bypass("bypass", "Bypasses most plugin restrictions", PermissionDefault.OP, true),

    access("access", "Access other portions of the plugin", PermissionDefault.TRUE, true),

    player_wildcard("player.*", "Access multiple player based commands", PermissionDefault.FALSE, new HashMap<>() {{
        put("crazyauctions.bid", true);
        put("crazyauctions.sell", true);
        put("crazyauctions.access", true);
        put("crazyauctions.view", true);
    }}, true),

    admin_wildcard("admin", "Access multiple admin based commands", PermissionDefault.FALSE, new HashMap<>() {{
        put("crazyauctions.player.*", true);
        put("crazyauctions.reload", true);
        put("crazyauctions.bypass", true);
        put("crazyauctions.view", true);
    }}, true);

    private final String node;
    private final String description;
    private final PermissionDefault isDefault;
    private final Map<String, Boolean> children;

    private final boolean register;

    private final PluginManager manager = CrazyAuctions.getPlugin().getServer().getPluginManager();

    Permissions(String node, String description, PermissionDefault isDefault, Map<String, Boolean> children, boolean register) {
        this.node = node;
        this.description = description;

        this.isDefault = isDefault;

        this.children = children;
        this.register = register;
    }

    Permissions(String node, String description, PermissionDefault isDefault, boolean register) {
        this.node = node;
        this.description = description;

        this.isDefault = isDefault;
        this.children = new HashMap<>();
        this.register = register;
    }

    public final String getNode() {
        return "crazyauctions." + this.node;
    }

    public final boolean shouldRegister() {
        return this.register;
    }

    public final String getDescription() {
        return this.description;
    }

    public final PermissionDefault isDefault() {
        return this.isDefault;
    }

    public final Map<String, Boolean> getChildren() {
        return this.children;
    }

    public final boolean hasPermission(final Player player) {
        return player.hasPermission(getNode());
    }

    public final boolean isValid() {
        return this.manager.getPermission(getNode()) != null;
    }

    public final Permission getPermission() {
        return new Permission(getNode(), getDescription(), isDefault(), getChildren().isEmpty() ? null : getChildren());
    }

    public void registerPermission() {
        if (isValid()) return;

        this.manager.addPermission(getPermission());
    }

    public void unregisterPermission() {
        if (!isValid()) return;

        this.manager.removePermission(getNode());
    }
}