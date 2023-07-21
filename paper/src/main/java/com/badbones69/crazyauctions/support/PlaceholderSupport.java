package com.badbones69.crazyauctions.support;

import com.badbones69.crazyauctions.ApiManager;
import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.config.types.PluginConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaceholderSupport {

    private static final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    public static String setPlaceholders(String placeholder) {
        placeholder = placeholder.replaceAll("\\{prefix}", ApiManager.getPluginConfig().getProperty(PluginConfig.COMMAND_PREFIX));

        return placeholder;
    }
}