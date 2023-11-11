package com.badbones69.crazyauctions.utils.misc;

import com.badbones69.crazyauctions.ApiManager;
import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.config.types.PluginConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class ColorUtils {

    private static final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    public static String getPrefix() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.COMMAND_PREFIX);
    }
}