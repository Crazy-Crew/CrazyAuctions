package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.command.PaperConsole;
import com.badbones69.crazyauctions.player.PaperPlayerListener;
import com.badbones69.crazyauctions.player.PaperPlayerRegistry;
import com.ryderbelserion.ithildin.core.Console;
import com.ryderbelserion.ithildin.core.IthildinCore;
import com.ryderbelserion.ithildin.core.utils.LoggerUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Field;
import java.nio.file.Path;

public class CrazyAuctions extends JavaPlugin implements IthildinCore {

    private static CrazyAuctions plugin;

    private final PaperConsole paperConsole;

    private PaperPlayerRegistry paperPlayerRegistry;

    public CrazyAuctions() {
        super();

        try {
            Field api = Provider.class.getDeclaredField("api");
            api.setAccessible(true);
            api.set(null, this);
        } catch (Exception exception) {
            exception.printStackTrace();

            getServer().getPluginManager().disablePlugin(this);
        }

        plugin = this;

        paperConsole = new PaperConsole();

        LoggerUtils.setLoggerName(plugin.getName());

        Builder.start();
    }

    @Override
    public void onEnable() {
        // Do whatever else.

        // Register listeners.
        PaperPlayerListener paperPlayerListener = new PaperPlayerListener();

        getServer().getPluginManager().registerEvents(paperPlayerListener, this);

        // Set up registries.
        this.paperPlayerRegistry = new PaperPlayerRegistry();
    }

    @Override
    public void onDisable() {
        // Stop the plugin.
        Builder.stop();
    }

    @Override
    public @NotNull String getCurrentVersion() {
        return getDescription().getVersion();
    }

    @Override
    public @NotNull Path getDirectory() {
        return getDataFolder().toPath();
    }

    @Override
    public @NotNull Path getStorage() {
        return getDirectory().resolve("storage");
    }

    public @NotNull Path getUserData() {
        return getStorage().resolve("userdata");
    }

    @Override
    public @NotNull Console getConsole() {
        return this.paperConsole;
    }

    @Override
    public @NotNull PaperPlayerRegistry getPlayerRegistry() {
        return this.paperPlayerRegistry;
    }

    public @NotNull static CrazyAuctions getPlugin() {
        return plugin;
    }
}