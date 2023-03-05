package us.crazycrew.crazyauctions.commands;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import us.crazycrew.crazyauctions.api.interfaces.Universal;
import us.crazycrew.crazyauctions.configurations.PluginSettings;
import us.crazycrew.crazyauctions.loader.AuctionsStarter;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Date: 3/4/2023
 * Time: 10:22 PM
 * Last Edited: 3/4/2023 @ 10:22 PM
 *
 * Description: The permissions
 */
public enum Permissions implements Universal {

    AUCTIONS_COMMAND_PLAYER_MENU("command.player.menu", "Opens the auction house menu.", null, PermissionDefault.TRUE);

    private final String node;
    private final String description;
    private final HashMap<String, Boolean> children;
    private final PermissionDefault permissionDefault;

    private static final String prefix = AuctionsStarter.getPluginConfig().getProperty(PluginSettings.COMMAND_PERMISSION);;

    /**
     * @param node permission node without the prefix
     * @param description description of the permission
     * @param children sub permissions
     * @param permissionDefault true, false, op, not-op
     */
    Permissions(String node, String description, HashMap<String, Boolean> children, PermissionDefault permissionDefault) {
        this.node = node;
        this.description = description;
        this.children = children;
        this.permissionDefault = permissionDefault;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return individual node without the prefix i.e command.admin.help
     */
    public String getNode() {
        return this.node;
    }

    /**
     * @return permission default i.e true, false, op, not op
     */
    public PermissionDefault getPermissionDefault() {
        return this.permissionDefault;
    }

    /**
     * @return sub permissions of the main permission
     */
    public HashMap<String, Boolean> getChildren() {
        return this.children;
    }

    /**
     * @return completed permission node
     */
    public String getPermissionNode() {
        return prefix + "." + this.node;
    }

    /**
     * Registers all permissions into the plugin manager.
     *
     * @param pluginManager server's plugin manager
     */
    public static void register(PluginManager pluginManager) {
        EnumSet.allOf(Permissions.class).forEach(action -> {
            if (pluginManager.getPermission(action.getPermissionNode()) == null) return;
            pluginManager.addPermission(
                    new Permission(
                            action.getPermissionNode(),
                            action.getDescription(),
                            action.getPermissionDefault(),
                            action.getChildren()
                    ));
        });
    }
}