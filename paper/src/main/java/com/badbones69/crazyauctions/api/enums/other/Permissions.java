package com.badbones69.crazyauctions.api.enums.other;

import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.kyori.permissions.enums.PermissionType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;
import java.util.Map;

public enum Permissions {

    reload("reload", "Access to /crazyauctions reload", PermissionDefault.OP, false),
    help("help", "Access to /crazyauctions help", PermissionDefault.TRUE, false),
    view("view", "Access to /crazyauctions view", PermissionDefault.TRUE, false),
    expired("expired", "Access to /crazyauctions expired", PermissionDefault.TRUE, false),
    collect("collect", "Access to /crazyauctions collect", PermissionDefault.TRUE, false),
    listed("listed", "Access to /crazyauctions listed", PermissionDefault.TRUE, false),
    sell("sell", "Access to /crazyauctions sell", PermissionDefault.TRUE, false),
    bid("bid", "Access to /crazyauctions bid", PermissionDefault.TRUE, false),
    use("use", "Access to /crazyauctions", PermissionDefault.TRUE, false),

    force_end("force-end-all", "Access to /crazyauctions force-end-all", PermissionDefault.OP, false),

    bypass("bypass", "Bypasses most plugin restrictions", PermissionDefault.FALSE, true),

    access("access", "Access other portions of the plugin", PermissionDefault.TRUE, true),

    player_wildcard("player", "Access multiple player based commands", PermissionDefault.FALSE, Map.ofEntries(
            Map.entry("crazyauctions.bid", true),
            Map.entry("crazyauctions.sell", true),
            Map.entry("crazyauctions.access", true),
            Map.entry("crazyauctions.view", true)
    ), true),

    admin_wildcard("admin", "Access multiple admin based commands", PermissionDefault.FALSE, Map.ofEntries(
            Map.entry("crazyauctions.player.*", true),
            Map.entry("crazyauctions.reload", true),
            Map.entry("crazyauctions.bypass", true),
            Map.entry("crazyauctions.view", true)
    ), true);

    private final String node;
    private final String description;
    private final PermissionDefault isDefault;
    private final Map<String, Boolean> children;

    private final boolean register;

    private final FusionPaper fusion = (FusionPaper) FusionProvider.getInstance();

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

    public final boolean isRegister() {
        return this.register;
    }

    public final String getDescription() {
        return this.description;
    }

    public final PermissionDefault getDefault() {
        return this.isDefault;
    }

    public final Map<String, Boolean> getChildren() {
        return this.children;
    }

    public final boolean hasPermission(final Player player) {
        return player.hasPermission(getNode());
    }

    public final Permission getPermission() {
        return new Permission(getNode(), getDescription(), getDefault(), getChildren().isEmpty() ? null : getChildren());
    }

    public final PermissionContext getContext() {
        final PermissionContext context = new PermissionContext(
                getNode(),
                getDescription(),
                switch (getDefault()) {
                    case TRUE -> PermissionType.TRUE;
                    case FALSE -> PermissionType.FALSE;
                    case OP -> PermissionType.OP;
                    case NOT_OP -> PermissionType.NOT_OP;
                }
        );

        getChildren().forEach(context::addPermission);

        return context;
    }

    public void registerPermission() {
        if (!this.register) return;

        this.fusion.registerPermission(getContext());
    }
}