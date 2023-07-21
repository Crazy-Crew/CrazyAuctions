package com.badbones69.crazyauctions.support;

import com.badbones69.crazyauctions.ApiManager;
import com.badbones69.crazyauctions.config.types.PluginConfig;

public class PlaceholderSupport {

    public static String setPlaceholders(String placeholder) {
        placeholder = placeholder.replaceAll("\\{prefix}", ApiManager.getPluginConfig().getProperty(PluginConfig.COMMAND_PREFIX));

        return placeholder;
    }
}