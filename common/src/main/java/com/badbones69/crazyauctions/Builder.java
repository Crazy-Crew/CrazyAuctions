package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.configuration.Config;
import com.badbones69.crazyauctions.configuration.Locale;
import com.ryderbelserion.ithildin.core.IthildinCore;

public class Builder {

    public static void start() {
        Config.handle(IthildinCore.api().getDirectory());
        Locale.handle(IthildinCore.api().getDirectory().resolve("locale"));

        if (Config.VERBOSE) System.out.println("Yay!");
    }

    public static void stop() {

    }
}