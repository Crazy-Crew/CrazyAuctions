package us.crazycrew.crazyauctions.api.enums;

import us.crazycrew.crazyauctions.CrazyAuctions;
import us.crazycrew.crazyauctions.api.economy.vault.VaultSupport;
import us.crazycrew.crazyauctions.utils.utilities.misc.ColorUtils;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginSupport {

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();

    private final Map<SupportedPlugins, Boolean> cachedPlugins = new HashMap<>();

    public void updateHooks() {
        cachedPlugins.clear();

        for (SupportedPlugins supportedPlugin : SupportedPlugins.values()) {
            if (supportedPlugin.isPluginLoaded() && supportedPlugin.getLoadedPlugin().isEnabled()) {

                String website = supportedPlugin.getLoadedPlugin().getDescription().getWebsite();
                List<String> author = supportedPlugin.getLoadedPlugin().getDescription().getAuthors();
                String name = supportedPlugin.getLoadedPlugin().getDescription().getName();
                String main = supportedPlugin.getLoadedPlugin().getDescription().getMain();

                switch (supportedPlugin) {
                    default: supportedPlugin.addPlugin(true);
                }
            } else {
                supportedPlugin.addPlugin(false);
            }
        }

        printHooks();
    }

    public void printHooks() {
        if (cachedPlugins.isEmpty()) updateHooks();

        plugin.getLogger().info(ColorUtils.color("&8&l=== &e&lCrazyAuctions Hook Status &8&l==="));

        cachedPlugins.keySet().forEach(value -> {
            if (value.isPluginLoaded()) {
                plugin.getLogger().info(ColorUtils.color("&6&l" + value.name() + " &a&lFOUND"));
            } else {
                plugin.getLogger().info(ColorUtils.color("&6&l" + value.name() + " &c&lNOT FOUND"));
            }
        });
    }

    /**
     * Loads the currency if it is on the server.
     */
    public void loadCurrency() {
        for (SupportedPlugins supportedPlugin : SupportedPlugins.values()) {
            if (supportedPlugin.isPluginLoaded() && supportedPlugin.getLoadedPlugin().isEnabled()) {
                switch (supportedPlugin) {
                    case VAULT: this.plugin.getStarter().setVaultSupport(new VaultSupport()).init();
                }

                return;
            }
        }

        plugin.getLogger().warning("No economy plugin found, Any economy based feature will not work.");
    }

    public enum SupportedPlugins {
        // Economy Plugins
        VAULT("Vault"),

        // Region Protection
        WORLDGUARD("WorldGuard"),
        WORLDEDIT("WorldEdit");

        private final String pluginName;

        SupportedPlugins(String pluginName) {
            this.pluginName = pluginName;
        }

        private final CrazyAuctions plugin = CrazyAuctions.getPlugin();

        private final PluginSupport pluginSupport = plugin.getPluginSupport();

        public boolean isPluginLoaded() {
            return plugin.getServer().getPluginManager().getPlugin(pluginName) != null;
        }

        public Plugin getLoadedPlugin() {
            return plugin.getServer().getPluginManager().getPlugin(pluginName);
        }

        public boolean isCachedPluginLoaded() {
            return pluginSupport.cachedPlugins.get(this);
        }

        public void addPlugin(boolean value) {
            pluginSupport.cachedPlugins.put(this, value);
        }

        public void removePlugin() {
            pluginSupport.cachedPlugins.remove(this);
        }

        public boolean isPluginEnabled() {
            return pluginSupport.cachedPlugins.get(this);
        }
    }
}