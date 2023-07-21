package com.badbones69.crazyauctions.utils.misc;

import com.badbones69.crazyauctions.ApiManager;
import com.badbones69.crazyauctions.api.interfaces.Universal;
import com.badbones69.crazyauctions.config.types.PluginConfig;

public class ColorUtils implements Universal {

    public static String getPrefix() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.COMMAND_PREFIX);
    }
}